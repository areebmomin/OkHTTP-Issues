package com.areeb.okhttpissueslist.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.areeb.okhttpissueslist.R
import com.areeb.okhttpissueslist.activities.IssueDescriptionActivity
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.API_DATE_FORMAT
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.APP_DATE_FORMAT
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_DESCRIPTION
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_NUMBER
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.ISSUE_TITLE
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.LAST_UPDATE_TIME
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.USER_AVATAR_URL
import com.areeb.okhttpissueslist.activities.MainActivity.Companion.USER_NAME
import com.areeb.okhttpissueslist.database.DatabaseIssuesList
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class IssuesListAdapter(private val context: Context) :
    PagingDataAdapter<DatabaseIssuesList, IssuesListAdapter.ViewHolder>(
        IssuesListAdapterDiffUtilCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.issues_list_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //get DatabaseIssuesList object
        val databaseIssue: DatabaseIssuesList? = getItem(position)

        //set data if databaseIssue is not null
        databaseIssue?.let {
            //set user avatar
            Picasso.get()
                .load(it.avatar_url)
                .resize(32, 32)
                .centerCrop()
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(holder.userAvatarImageView)

            //set issue title
            holder.titleTextView.text = it.title

            //set description
            holder.descriptionTextView.text =
                if (it.body.length > 200)
                    "${it.body.subSequence(0, 200)} ..."
                else it.body

            //set user name
            holder.userNameTextView.text = it.userName

            //set updated at
            val date = SimpleDateFormat(API_DATE_FORMAT, Locale.US).parse(it.updated_at)
            date?.let {
                val formattedDate = SimpleDateFormat(APP_DATE_FORMAT, Locale.US).format(date)
                holder.updatedAtTextView.text = formattedDate
            }

            //itemView onCLick listener
            holder.itemView.setOnClickListener {_ ->
                val intent = Intent(context, IssueDescriptionActivity::class.java)
                intent.putExtra(ISSUE_NUMBER, it.issueNumber)
                intent.putExtra(ISSUE_TITLE, it.title)
                intent.putExtra(ISSUE_DESCRIPTION, it.body)
                intent.putExtra(USER_AVATAR_URL, it.avatar_url)
                intent.putExtra(USER_NAME, it.userName)
                intent.putExtra(LAST_UPDATE_TIME, it.updated_at)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userAvatarImageView: ImageView = itemView.findViewById(R.id.userAvatarImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val updatedAtTextView: TextView = itemView.findViewById(R.id.updatedAtTextView)
    }
}

internal class IssuesListAdapterDiffUtilCallback :
    DiffUtil.ItemCallback<DatabaseIssuesList>() {
    override fun areItemsTheSame(
        oldItem: DatabaseIssuesList,
        newItem: DatabaseIssuesList
    ): Boolean {
        return oldItem.issueId == newItem.issueId
    }

    override fun areContentsTheSame(
        oldItem: DatabaseIssuesList,
        newItem: DatabaseIssuesList
    ): Boolean {
        return oldItem == newItem
    }
}