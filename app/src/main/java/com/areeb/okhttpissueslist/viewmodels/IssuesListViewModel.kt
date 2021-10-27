package com.areeb.okhttpissueslist.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.areeb.okhttpissueslist.database.getDatabase
import com.areeb.okhttpissueslist.repositories.IssuesListRepository
import kotlinx.coroutines.launch

class IssuesListViewModel(application: Application) : ViewModel() {

    //get database and initialize repository
    private val database = getDatabase(application)
    private val issuesListRepository =
        IssuesListRepository(database)

    //mutable live data variables
    private val _errorResponse = MutableLiveData<Exception>()

    //live data variables
    val issuesListResponse = issuesListRepository.issuesListResponse
    val errorResponse: LiveData<Exception> = _errorResponse

    init {
        fetchIssuesList()
    }

    //method to fetch business list
    private fun fetchIssuesList() {
        viewModelScope.launch {
            try {
                issuesListRepository.fetchIssuesList()
            } catch (e: Exception) {
                _errorResponse.value = e
                Log.d("LOG_TAG", e.toString())
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class IssuesListViewModelFactory(
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssuesListViewModel(application) as T
    }
}