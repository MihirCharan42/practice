package com.example.practice.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice.model.data.Product
import com.example.practice.model.data.Products
import com.example.practice.model.ProductsRepository
import com.example.practice.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class ProductsViewmodel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    private var skipValue = 0
    private var limit = 30
    private var searchSkipValue = 0
    private var searchLimit = 30
    var isLastPage = false
    var isSearchLastPage = false
    var isLoading = false
    private val productsState = MutableLiveData<ApiResult<Products>>()
    private val favoritesState = MutableLiveData<ApiResult<ArrayList<Product>>>()
    private val editFavoritesState = MutableLiveData<ApiResult<String>>()
    private val nextProductsState = MutableLiveData<ApiResult<Products>>()
    private val searchProductsState = MutableLiveData<ApiResult<Products>>()
    private val nextSearchProductsState = MutableLiveData<ApiResult<Products>>()
    var job: Job? = null

    fun getProductsState(): LiveData<ApiResult<Products>> = productsState

    fun getNextProductsState(): LiveData<ApiResult<Products>> = nextProductsState

    fun getSearchProductsState(): LiveData<ApiResult<Products>> = searchProductsState

    fun getNextSearchProductsState(): LiveData<ApiResult<Products>> = nextSearchProductsState

    fun getFavoritesStateState(): LiveData<ApiResult<ArrayList<Product>>> = favoritesState

    fun refreshProducts() {
        skipValue = 0
        getProducts()
    }

    fun getProducts() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            productsState.postValue(ApiResult.Loading())
            val res = async { productsRepository.getProducts(limit, skipValue) }.await()
            with(res) {
                hideLoading()
                if (isSuccessful && body() != null) {
                    productsState.postValue(ApiResult.Success(body()))
                } else {
                    productsState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun searchProducts(query: String?) {
        job?.cancel()
        showLoading()
        searchSkipValue = 0
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(200)
            searchProductsState.postValue(ApiResult.Loading())
            val res = async { productsRepository.searchProducts(query, searchLimit, searchSkipValue) }.await()
            with(res) {
                hideLoading()
                if(isSuccessful) {
                    body()?.run {
                        searchProductsState.postValue(ApiResult.Success(this))
                        total?.let {
                            if(it <= searchSkipValue) isSearchLastPage = true
                        }
                    }
                } else {
                    searchProductsState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun loadNextSearchPageData(query: String) {
        showLoading()
        searchSkipValue += searchLimit
        viewModelScope.launch(Dispatchers.IO) {
            nextSearchProductsState.postValue(ApiResult.Loading())
            val res = async { productsRepository.searchProducts(query, searchLimit, searchSkipValue) }.await()
            with(res) {
                hideLoading()
                if (isSuccessful) {
                    body()?.run {
                        nextSearchProductsState.postValue(ApiResult.Success(this))
                        total?.let {
                            if (it <= searchSkipValue) isSearchLastPage = true
                        }
                    }
                } else {
                    nextSearchProductsState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun loadNextPageData() {
        showLoading()
        skipValue += limit
        viewModelScope.launch(Dispatchers.IO) {
            nextProductsState.postValue(ApiResult.Loading())
            val res = async { productsRepository.getProducts(limit, skipValue) }.await()
            with(res) {
                hideLoading()
                if (isSuccessful) {
                    body()?.run {
                        nextProductsState.postValue(ApiResult.Success(this))
                        total?.let {
                            if(total <= skipValue)
                                isLastPage = true
                        }
                    }
                } else {
                    nextProductsState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun getFavourites() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            favoritesState.postValue(ApiResult.Loading())
            supervisorScope {
                val res = async { productsRepository.getFavourites() }
                try {
                    favoritesState.postValue(ApiResult.Success(res.await()))
                } catch (e: Exception) {
                    favoritesState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun addFavourite(product: Product) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            favoritesState.postValue(ApiResult.Loading())
            supervisorScope {
                val res = async { productsRepository.addFavourites(product) }
                try {
                    editFavoritesState.postValue(ApiResult.Success("${product.title} is saved"))
                } catch (e: Exception) {
                    editFavoritesState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    fun deleteFavourite(product: Product) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            favoritesState.postValue(ApiResult.Loading())
            supervisorScope {
                val res = async { productsRepository.deleteFavourites(product) }
                try {
                    editFavoritesState.postValue(ApiResult.Success("${product.title} is deleted"))
                } catch (e: Exception) {
                    editFavoritesState.postValue(ApiResult.Error(message = "Something went wrong"))
                }
            }
        }
    }

    private fun showLoading() {
        isLoading = true
    }

    private fun hideLoading() {
        isLoading = false
    }
}