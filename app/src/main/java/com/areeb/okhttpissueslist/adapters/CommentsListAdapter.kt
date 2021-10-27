package com.areeb.okhttpissueslist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.areeb.okhttpissueslist.R
import com.areeb.okhttpissueslist.activities.MainActivity
import com.areeb.okhttpissueslist.database.DatabaseCommentsList
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class CommentsListAdapter :
    PagingDataAdapter<DatabaseCommentsList, CommentsListAdapter.ViewHolder>(
        CommentsListAdapterDiffUtilCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_list_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //get DatabaseIssuesList object
        val databaseComments: DatabaseCommentsList? = getItem(position)

        //set data if databaseIssue is not null
        databaseComments?.let {
            //set user avatar
            Picasso.get()
                .load(it.avatar_url)
                .resize(32, 32)
                .centerCrop()
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(holder.userAvatarImageView)

            //set description
            holder.descriptionTextView.text = it.body

            //set user name
            holder.userNameTextView.text = it.userName

            //set updated at
            val date =
                SimpleDateFormat(MainActivity.API_DATE_FORMAT, Locale.US).parse(it.updated_at)
            date?.let {
                val formattedDate =
                    SimpleDateFormat(MainActivity.APP_DATE_FORMAT, Locale.US).format(date)
                holder.updatedAtTextView.text = formattedDate
            }
        }
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userAvatarImageView: ImageView = itemView.findViewById(R.id.userAvatarImageView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val updatedAtTextView: TextView = itemView.findViewById(R.id.updatedAtTextView)
    }
}

internal class CommentsListAdapterDiffUtilCallback :
    DiffUtil.ItemCallback<DatabaseCommentsList>() {
    override fun areItemsTheSame(
        oldItem: DatabaseCommentsList,
        newItem: DatabaseCommentsList
    ): Boolean {
        return oldItem.commentId == newItem.commentId
    }

    override fun areContentsTheSame(
        oldItem: DatabaseCommentsList,
        newItem: DatabaseCommentsList
    ): Boolean {
        return oldItem == newItem
    }
}