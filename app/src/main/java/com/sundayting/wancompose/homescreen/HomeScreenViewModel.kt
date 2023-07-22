package com.sundayting.wancompose.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.homescreen.repo.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.resources.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repo: ArticleRepository,
) : ViewModel() {

    @Resource("/article")
    class Article {
        @Resource("list")
        class List(val parent: Article = Article()) {

            @Resource("{id}")
            class Id(val parent: List = List(), val id: Int) {
                @Resource("json")
                class Json(val parent: Id)
            }

        }
    }

    init {
        viewModelScope.launch {
            val result = runCatching {
                repo.fetchHomePageArticle(0)
            }
            result.getOrNull()?.let { bean ->
                bean.data.list.forEach {
                    Log.d("临时测试", it.title)
                }
            }
        }

    }

}