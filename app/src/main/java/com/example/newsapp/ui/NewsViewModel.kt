package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepo: NewsRepository) : ViewModel() {

    val breakingNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val breakingNewsPageNumber = 1

    val searchNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNewsPageNumber = 1

    init {
        getBreakingNews("in")
    }

    private fun getBreakingNews(countryCode: String) =
        viewModelScope.launch {
            breakingNewsLiveData.postValue(Resource.Loading())
            val response = newsRepo.getBreakingNews(countryCode, breakingNewsPageNumber)
            breakingNewsLiveData.postValue(handleResponse(response))
        }

    fun searchNews(searchQuery: String) =
        viewModelScope.launch {
            searchNewsLiveData.postValue(Resource.Loading())
            val response = newsRepo.searchNews(searchQuery, searchNewsPageNumber)
            searchNewsLiveData.postValue(handleResponse(response))
        }

    private fun handleResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                return Resource.Success(newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepo.upsert(article)
    }

    fun getSavedArticles() = newsRepo.getSavedArticles()

    fun deleteArticles(article: Article) = viewModelScope.launch {
        newsRepo.deleteArticle(article)
    }
}