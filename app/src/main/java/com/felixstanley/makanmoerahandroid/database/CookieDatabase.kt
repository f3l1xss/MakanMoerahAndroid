package com.felixstanley.makanmoerahandroid.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.database.dao.CookieDatabaseDao
import com.felixstanley.makanmoerahandroid.database.entity.CookieDatabaseEntity

@Database(entities = [CookieDatabaseEntity::class], version = 1, exportSchema = false)
abstract class CookieDatabase : RoomDatabase() {

    abstract val cookieDatabaseDao: CookieDatabaseDao

    companion object {

        @Volatile
        private var instance: CookieDatabase? = null

        fun getInstance(context: Context): CookieDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CookieDatabase::class.java,
                        Constants.COOKIE_DATABASE_NAME
                    )
                        // Allow Main Thread Queries As Latest Cookies needs to be synchronously
                        // retrieved before subsequent HTTP API Calls
                        .allowMainThreadQueries().fallbackToDestructiveMigration().build()
                }
                return instance as CookieDatabase
            }
        }
    }
}