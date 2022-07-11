package com.gourav.currencyconverter.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.utils.Constants

@Database(entities = [CurrencyModel::class], version = 1)
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
                    Constants.DB_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return dbInstance!!
        }
    }
}