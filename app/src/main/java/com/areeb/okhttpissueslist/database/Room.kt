package com.areeb.okhttpissueslist.database

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface IssuesListDao {
    @Query("select * from databaseissueslist")
    fun getAllIssues(): PagingSource<Int, DatabaseIssuesList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIssues(databaseIssuesList: List<DatabaseIssuesList>)
}

@Dao
interface CommentsListDao {
    @Query("select * from databasecommentslist where issueId = :issuesNumber")
    fun getAllComments(issuesNumber: String): PagingSource<Int, DatabaseCommentsList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(databaseCommentsList: List<DatabaseCommentsList>)
}

//App Version = 1.0 has Room Database Version = 1

@Database(
    entities = [DatabaseIssuesList::class, DatabaseCommentsList::class],
    version = 1,
    exportSchema = true
)

abstract class OkHTTPIssueDatabase : RoomDatabase() {
    abstract val issuesListDao: IssuesListDao
    abstract val commentsListDao: CommentsListDao
}

private lateinit var DATABASE_INSTANCE: OkHTTPIssueDatabase

fun getDatabase(context: Context): OkHTTPIssueDatabase {
    synchronized(OkHTTPIssueDatabase::class.java) {
        if (!::DATABASE_INSTANCE.isInitialized) {
            DATABASE_INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                OkHTTPIssueDatabase::class.java, "okhttpissues"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return DATABASE_INSTANCE
}