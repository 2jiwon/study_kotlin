package com.fancytank.mypaws.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fancytank.mypaws.data.dao.PetDao
import com.fancytank.mypaws.data.dao.UserDao
import com.fancytank.mypaws.data.entity.Pet
import com.fancytank.mypaws.data.entity.User

@Database(entities = [User::class, Pet::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                Log.d("AppDatabase ", "Database instance created")

                instance
            }
        }
    }
}