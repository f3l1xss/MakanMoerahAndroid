package com.felixstanley.makanmoerahandroid.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.felixstanley.makanmoerahandroid.database.entity.CookieDatabaseEntity

@Dao
interface CookieDatabaseDao {

    @Query("SELECT * FROM cookie ORDER BY name ASC")
    fun getAllCookiesAsync(): LiveData<List<CookieDatabaseEntity>>

    @Query("SELECT * FROM cookie ORDER BY name ASC")
    fun getAllCookiesSynchronous(): List<CookieDatabaseEntity>

    @Insert
    suspend fun insert(cookie: CookieDatabaseEntity)

    @Query("DELETE FROM cookie WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("DELETE FROM cookie")
    suspend fun clear()
}