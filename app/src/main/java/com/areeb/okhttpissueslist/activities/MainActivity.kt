package com.areeb.okhttpissueslist.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.areeb.okhttpissueslist.R
import com.areeb.okhttpissueslist.adapters.IssuesListAdapter
import com.areeb.okhttpissueslist.database.DatabaseIssuesList
import com.areeb.okhttpissueslist.databinding.ActivityMainBinding
import com.areeb.okhttpissueslist.viewmodels.IssuesListViewModel
import com.areeb.okhttpissueslist.viewmodels.IssuesListViewModelFactory
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    companion object {
        const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val APP_DATE_FORMAT = "MM-dd-yyyy"
        const val ISSUE_NUMBER = "ISSUE_NUMBER"
        const val ISSUE_TITLE = "ISSUE_TITLE"
        const val ISSUE_DESCRIPTION = "ISSUE_DESCRIPTION"
        const val USER_AVATAR_URL = "USER_AVATAR_URL"
        const val USER_NAME = "USER_NAME"
        const val LAST_UPDATE_TIME = "LAST_UPDATE_TIME"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var issueListAdapter: IssuesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        //initialize view model
        val issuesListViewModel = ViewModelProvider(
            this, IssuesListViewModelFactory(
                application
            )
        ).get(
            IssuesListViewModel::class.java
        )

        //initialize and set adapter
        issueListAdapter = IssuesListAdapter(this)
        binding.issuesListRecyclerView.adapter = issueListAdapter

        issueListAdapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading
                && it.append.endOfPaginationReached && issueListAdapter.itemCount < 1
            ) {
                //hide recycler view
                binding.issuesListRecyclerView.visibility = View.GONE

                //show description
                binding.noIssuesFoundTextView.visibility = View.VISIBLE
            } else if (it.source.refresh is LoadState.NotLoading
                && it.append.endOfPaginationReached
                && issueListAdapter.itemCount > 0
            ) {
                //show recycler view
                binding.issuesListRecyclerView.visibility = View.VISIBLE

                //hide description
                binding.noIssuesFoundTextView.visibility = View.GONE
            }
        }

        //fetch all issues response observer
        issuesListViewModel.issuesListResponse.observe(this, fetchAllIssuesResponseObserver())

        // error response observer
        issuesListViewModel.errorResponse.observe(this, errorResponseObserver())
    }

    private fun fetchAllIssuesResponseObserver(): Observer<PagingData<DatabaseIssuesList>> {
        return Observer<PagingData<DatabaseIssuesList>> { databaseIssuesLists: PagingData<DatabaseIssuesList> ->
            //submit customer list
            issueListAdapter.submitData(lifecycle, databaseIssuesLists)
        }
    }

    private fun errorResponseObserver(): Observer<Exception> {
        return Observer { exception: Exception? ->
            if (exception is UnknownHostException) {
                Toast.makeText(
                    this,
                    getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (exception != null) {
                Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                Log.d("LOG_TAG", exception.message ?: "")
            }
        }
    }
}