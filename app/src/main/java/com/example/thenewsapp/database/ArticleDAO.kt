package com.example.thenewsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.thenewsapp.models.Article

@Dao
interface ArticleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles WHERE isFavourite = 1")
    fun getFavourites(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE isFavourite = 1")
    suspend fun getFavouritesList(): List<Article>

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles")
    suspend fun getAllArticlesList(): List<Article>

    @Query("DELETE FROM articles WHERE isFavourite = 0")
    suspend fun clearHeadlines()

    @Delete
    suspend fun deleteArticle(article: Article)
}