package com.areeb.okhttpissueslist.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseIssuesList(
    @PrimaryKey
    val issueId: String,
    val issueNumber: String,
    val title: String,
    val comments_url: String,
    val updated_at: String,
    val body: String,
    val userName: String,
    val userId: String,
    val avatar_url: String
)

@Entity
data class DatabaseCommentsList(
    @PrimaryKey
    val commentId: String,
    val issueId: String,
    val userId: String,
    val userName: String,
    val avatar_url: String,
    val updated_at: String,
    val body: String
)