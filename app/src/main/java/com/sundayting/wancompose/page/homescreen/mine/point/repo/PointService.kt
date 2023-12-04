package com.sundayting.wancompose.page.homescreen.mine.point.repo

import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanNResult
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.Serializable

interface PointService {

    @GET("/lg/coin/list/{page}/json")
    suspend fun fetchPointList(@Path("page") page: Int): NResult<PointResult>

    @Serializable
    data class PointData(
        val curPage: Int,
        val datas: List<PointItemData>,
        val pageCount: Int,
    )

    @Serializable
    data class PointItemData(
        val coinCount: Int,
        val date: Long,
        val reason: String,
        val id: Int,
    )

    @Serializable
    class PointResult(
        override val data: PointData?,
        override val errorCode: Int,
        override val errorMsg: String,
    ) : WanNResult<PointData>()


}