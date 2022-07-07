package com.gourav.currencyconverter.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gourav.currencyconverter.data.models.CurrencyModel

@Database(entities = [CurrencyModel::class], version = 2)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDao(): AppDAO

    companion object {
        private var dbInstance: AppDatabase? = null

        fun getDbInstance(context: Context): AppDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Currency_DB"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return dbInstance!!
        }
    }
}