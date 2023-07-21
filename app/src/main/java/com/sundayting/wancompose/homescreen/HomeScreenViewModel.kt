package com.sundayting.wancompose.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.Ktor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    @Resource("/article")
    class Article {
        @Resource("list")
        class List(val parent: Article = Article()) {

            @Resource("{id}")
            class Id(val parent: List = List(), val id: Long) {
                @Resource("json")
                class Json(val parent: Id)
            }

        }
    }

    init {
        viewModelScope.launch {
            runCatching {
                val article = Ktor.client.get(
                    Article.List.Id.Json(
                        Article.List.Id(
                            Article.List(), 0
                        )
                    )
                )
                val resultBean: HomePageArticleBean = article.body()
                resultBean.data?.let { it ->
                    it.list.forEach { articleBean ->
                        Log.d("临时测试", articleBean.title)
                    }
                }
            }
        }

    }

}