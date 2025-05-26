package com.sundayting.wancompose.common.helper

import com.sundayting.wancompose.function.UserLoginFunction.VISITOR_ID
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import com.tencent.mmkv.MMKV
import java.util.concurrent.ConcurrentHashMap

/**
 * MMKV的辅助工具
 *
 * 如果想访问项目通用的字段，那就直接通过[MMKVHelper]来调用api
 *
 * 如果想访问用户账号特定的字段，那就通过[MMKVHelper.userSpecific]的方式来调用api（⚠️如果没登录或者获取不到uid的情况下，访问的也是通用字段）
 */
object MMKVHelper : SharedPreferencesOperation by MMKVSpOperationDelegate(MMKV.defaultMMKV()) {

    private val mineRepo by lazy {
        MineRepository.getInstanceFromApp()
    }
    private val userSpecificCache = ConcurrentHashMap<String, SharedPreferencesOperation>()

    val userSpecific: SharedPreferencesOperation
        get() {
            val currentUid = mineRepo.curUidFlow.value
            //如果没登录，那么返回公用仓库
            if (currentUid == VISITOR_ID) {
                return this
            }
            return userSpecificCache.computeIfAbsent(currentUid.toString()) { uid ->
                // 如果缓存中没有，则创建新的实例
                MMKVSpOperationDelegate(MMKV.mmkvWithID(uid))
            }
        }

}


private class MMKVSpOperationDelegate(
    private val kv: MMKV
) : SharedPreferencesOperation {

    override fun saveBool(key: String, value: Boolean) = kv.encode(key, value)

    override fun saveInt(key: String, value: Int) = kv.encode(key, value)

    override fun saveLong(key: String, value: Long) = kv.encode(key, value)

    override fun saveFloat(key: String, value: Float) = kv.encode(key, value)

    override fun saveDouble(key: String, value: Double) = kv.encode(key, value)

    override fun saveString(key: String, value: String) = kv.encode(key, value)

    override fun getBool(key: String, defaultValue: Boolean) = kv.decodeBool(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int) = kv.decodeInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long) = kv.decodeLong(key, defaultValue)

    override fun getFloat(key: String, defaultValue: Float) = kv.decodeFloat(key, defaultValue)

    override fun getDouble(key: String, defaultValue: Double) = kv.decodeDouble(key, defaultValue)

    override fun getString(key: String, defaultValue: String?) = if (defaultValue == null) {
        kv.decodeString(key)
    } else {
        kv.decodeString(key, defaultValue)
    }


}

/**
 * 操作sp的方法集
 */
interface SharedPreferencesOperation {

    fun saveBool(key: String, value: Boolean): Boolean

    fun saveInt(key: String, value: Int): Boolean

    fun saveLong(key: String, value: Long): Boolean

    fun saveFloat(key: String, value: Float): Boolean

    fun saveDouble(key: String, value: Double): Boolean

    fun saveString(key: String, value: String): Boolean

    fun getBool(key: String, defaultValue: Boolean = false): Boolean

    fun getInt(key: String, defaultValue: Int = 0): Int

    fun getLong(key: String, defaultValue: Long = 0): Long

    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    fun getString(key: String, defaultValue: String? = null): String?

}