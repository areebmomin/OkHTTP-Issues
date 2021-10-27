package com.areeb.okhttpissueslist.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.areeb.okhttpissueslist.R
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_DESCRIPTION
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_NUMBER
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_TITLE
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.LAST_UPDATE_TIME
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.USER_AVATAR_URL
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.USER_NAME
import com.areeb.okhttpissueslist.adapters.CommentsListAdapter
import com.areeb.okhttpissueslist.database.DatabaseCommentsList
import com.areeb.okhttpissueslist.databinding.ActivityIssueDescriptionBinding
import com.areeb.okhttpissueslist.viewmodels.IssueDescriptionActivityViewModel
import com.areeb.okhttpissueslist.viewmodels.IssueDescriptionActivityViewModelFactory
import com.squareup.picasso.Picasso
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class IssueDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIssueDescriptionBinding
    private lateinit var commentsListAdapter: CommentsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_issue_description)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        //set activity title
        title = getString(R.string.issue_description)

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //get intent data
        var issueNumber = ""
        var issueTitle = ""
        var issueDescription = ""
        var userAvatarURL = ""
        var userName = ""
        var lastUpdatedAt = ""
        intent?.let {
            issueNumber = it.getStringExtra(ISSUE_NUMBER) ?: ""
            issueTitle = it.getStringExtra(ISSUE_TITLE) ?: ""
            issueDescription = it.getStringExtra(ISSUE_DESCRIPTION) ?: ""
            userAvatarURL = it.getStringExtra(USER_AVATAR_URL) ?: ""
            userName = it.getStringExtra(USER_NAME) ?: ""
            lastUpdatedAt = it.getStringExtra(LAST_UPDATE_TIME) ?: ""
        }

        //initialize and set adapter
        commentsListAdapter = CommentsListAdapter()
        binding.commentsListRecyclerView.adapter = commentsListAdapter

        commentsListAdapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading
                && it.append.endOfPaginationReached && commentsListAdapter.itemCount < 1
            ) {
                //hide recycler view
                binding.commentsListRecyclerView.visibility = View.GONE

                //show description
                binding.noCommentsFoundTextView.visibility = View.VISIBLE
            } else if (it.source.refresh is LoadState.NotLoading
                && it.append.endOfPaginationReached
                && commentsListAdapter.itemCount > 0
            ) {
                //show recycler view
                binding.commentsListRecyclerView.visibility = View.VISIBLE

                //hide description
                binding.noCommentsFoundTextView.visibility = View.GONE
            }
        }

        //initialize view model
        val issueDescriptionActivityViewModel = ViewModelProvider(
            this, IssueDescriptionActivityViewModelFactory(
                application,
                issueNumber
            )
        ).get(
            IssueDescriptionActivityViewModel::class.java
        )

        //fetch all comments response observer
        issueDescriptionActivityViewModel.commentsListResponse.observe(
            this,
            fetchAllCommentsResponseObserver()
        )

        // error response observer
        issueDescriptionActivityViewModel.errorResponse.observe(this, errorResponseObserver())

        //set issue title
        binding.titleTextView.text = issueTitle

        //set issue description
        binding.descriptionTextView.text = issueDescription

        //set user avatar
        Picasso.get()
            .load(userAvatarURL)
            .resize(32, 32)
            .centerCrop()
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(binding.userAvatarImageView)

        //set user name
        binding.userNameTextView.text = userName

        //set last updated time
        val date = SimpleDateFormat(MainActivity.API_DATE_FORMAT, Locale.US).parse(lastUpdatedAt)
        date?.let {
            val formattedDate =
                SimpleDateFormat(MainActivity.APP_DATE_FORMAT, Locale.US).format(date)
            binding.updatedAtTextView.text = formattedDate
        }
    }

    private fun fetchAllCommentsResponseObserver(): androidx.lifecycle.Observer<PagingData<DatabaseCommentsList>> {
        return androidx.lifecycle.Observer<PagingData<DatabaseCommentsList>> {
            //submit customer list
            commentsListAdapter.submitData(lifecycle, it)
        }
    }

    private fun errorResponseObserver(): androidx.lifecycle.Observer<Exception> {
        return androidx.lifecycle.Observer { exception: Exception? ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}