package com.sundayting.wancompose.page.aboutme

import android.content.Context
import android.graphics.Picture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.helper.MediaStoreHelper
import com.sundayting.wancompose.common.ktx.saveToBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutMeViewModel @Inject constructor(
    private val mediaStoreHelper: MediaStoreHelper,
    @ApplicationContext context: Context,
    private val eventManager: EventManager,
) : ViewModel() {

    private val contentResolver = context.contentResolver
    private var job: Job? = null

    private fun savePic(picture: Picture, name: String) {
        if (job?.isActive == true) {
            eventManager.emitToast("保存中，请稍后")
            return
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            mediaStoreHelper.saveBitmap(
                contentResolver,
                picture.saveToBitmap(),
                displayName = name
            )
            eventManager.emitToast("保存成功！")
        }
    }

    fun saveWeChatPic(picture: Picture) {
        savePic(picture, "玩Compose微信打赏图片")
    }

    fun saveAlipayPic(picture: Picture) {
        savePic(picture, "玩Compose支付宝打赏图片")
    }

}