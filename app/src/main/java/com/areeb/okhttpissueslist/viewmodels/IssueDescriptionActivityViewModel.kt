package com.areeb.okhttpissueslist.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.areeb.okhttpissueslist.database.getDatabase
import com.areeb.okhttpissueslist.repositories.IssueDescriptionActivityRepository
import kotlinx.coroutines.launch

class IssueDescriptionActivityViewModel(application: Application, issueNumber: String) :
    ViewModel() {

    //get database and initialize repository
    private val database = getDatabase(application)
    private val issueDescriptionActivityRepository =
        IssueDescriptionActivityRepository(database, issueNumber)

    //mutable live data variables
    private val _errorResponse = MutableLiveData<Exception>()

    //live data variables
    val commentsListResponse = issueDescriptionActivityRepository.commentsListResponse
    val errorResponse: LiveData<Exception> = _errorResponse

    init {
        fetchCommentsList()
    }

    //method to fetch business list
    private fun fetchCommentsList() {
        viewModelScope.launch {
            try {
                issueDescriptionActivityRepository.fetchCommentsList()
            } catch (e: Exception) {
                _errorResponse.value = e
                Log.d("LOG_TAG", e.toString())
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class IssueDescriptionActivityViewModelFactory(
    private val application: Application,
    private val issueNumber: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IssueDescriptionActivityViewModel(application, issueNumber) as T
    }
}