package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepo: NewsRepository) : ViewModel() {

    val breakingNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val pageNumber = 1

    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String) =
        viewModelScope.launch {
            breakingNewsLiveData.postValue(Resource.Loading())
            val response = newsRepo.getBreakingNews(countryCode, pageNumber)
            breakingNewsLiveData.postValue(handleResponse(response))
        }

    private fun handleResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                return Resource.Success(newsResponse)
            }
        }
        return Resource.Error(response.message())
    }
}