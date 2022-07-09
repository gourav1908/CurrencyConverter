package com.gourav.currencyconverter.di

import android.app.Application
import android.content.Context
import com.gourav.currencyconverter.data.network.ApiInterface
import com.gourav.currencyconverter.data.room.AppDatabase
import com.gourav.currencyconverter.data.room.AppDAO
import com.gourav.currencyconverter.repository.CurrencyRepository
import com.gourav.currencyconverter.repository.RepositoryInterface
import com.gourav.currencyconverter.utils.Constants.Companion.BASE_URL
import com.gourav.currencyconverter.utils.DispatchersInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideApiService(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): ApiInterface = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
        .create(ApiInterface::class.java)

    @Singleton
    @Provides
    fun providesMainRepository(
        apiInterface: ApiInterface,
        dao: AppDAO,
        @ApplicationContext context: Context
    ): RepositoryInterface = CurrencyRepository(apiInterface, dao, context)

    @Singleton
    @Provides
    fun provideAppDB(context: Application): AppDatabase {
        return AppDatabase.getDbInstance(context)
    }

    @Singleton
    @Provides
    fun getDAO(appDB: AppDatabase): AppDAO {
        return appDB.getDao()
    }

    @Singleton
    @Provides
    fun provideDispatchers(): DispatchersInterface = object : DispatchersInterface {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }

}