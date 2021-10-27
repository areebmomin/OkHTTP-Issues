package com.areeb.okhttpissueslist.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.areeb.okhttpissueslist.database.DatabaseIssuesList
import com.areeb.okhttpissueslist.database.OkHTTPIssueDatabase
import com.areeb.okhttpissueslist.network.PesaCashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IssuesListRepository(private val database: OkHTTPIssueDatabase) {

    //fetch collection date list LiveData
    val issuesListResponse: LiveData<PagingData<DatabaseIssuesList>> =
        Pager(PagingConfig(pageSize = 15)) {
            database.issuesListDao.getAllIssues()
        }.liveData

    //fetch issues list from api and insert in Room
    suspend fun fetchIssuesList() {
        withContext(Dispatchers.IO) {
            //get response form api
            val fetchIssuesListResponse = PesaCashApi.retrofitService.fetchIssuesList().body()

            //insert the fresh values in Room database
            fetchIssuesListResponse?.let { response ->
                //insert new values
                database.issuesListDao.insertIssues(response.map {
                    DatabaseIssuesList(
                        it.id,
                        it.number,
                        it.title,
                        it.comments_url,
                        it.updated_at,
                        it.body ?: "",
                        it.user.login,
                        it.user.id,
                        it.user.avatar_url
                    )
                })
            }
        }
    }
}