package com.areeb.okhttpissueslist.models

data class IssueListResponse(
    val id: String,
    val number: String,
    val title: String,
    val comments_url: String,
    val updated_at: String,
    val user: User,
    val body: String?
)

data class User(
    val login: String,
    val id: String,
    val avatar_url: String
)

data class CommentsListResponse(
    val id: String,
    val user: User,
    val updated_at: String,
    val body: String?
)