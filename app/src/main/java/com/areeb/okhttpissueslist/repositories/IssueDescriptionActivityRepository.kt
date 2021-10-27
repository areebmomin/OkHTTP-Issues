package com.areeb.okhttpissueslist.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.areeb.okhttpissueslist.database.DatabaseCommentsList
import com.areeb.okhttpissueslist.database.OkHTTPIssueDatabase
import com.areeb.okhttpissueslist.network.PesaCashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IssueDescriptionActivityRepository(
    private val database: OkHTTPIssueDatabase,
    private val issueNumber: String
) {
    //fetch collection date list LiveData
    val commentsListResponse: LiveData<PagingData<DatabaseCommentsList>> =
        Pager(PagingConfig(pageSize = 15)) {
            database.commentsListDao.getAllComments(issueNumber)
        }.liveData

    //fetch comments list from api and insert in Room
    suspend fun fetchCommentsList() {
        withContext(Dispatchers.IO) {
            //get response form api
            val fetchCommentsListResponse =
                PesaCashApi.retrofitService.fetchCommentsList(issueNumber).body()

            //insert the fresh values in Room database
            fetchCommentsListResponse?.let { response ->
                //insert new values
                database.commentsListDao.insertComments(response.map {
                    DatabaseCommentsList(
                        it.id,
                        issueNumber,
                        it.user.id,
                        it.user.login,
                        it.user.avatar_url,
                        it.updated_at,
                        it.body ?: ""
                    )
                })
            }
        }
    }
}