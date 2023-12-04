package com.sundayting.wancompose.page.homescreen.mine.point.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointRepository @Inject constructor(
    private val service: PointService,
) {

    /**
     * 获取积分列表
     */
    suspend fun fetchPointList(page: Int) = service.fetchPointList(page)

}