package com.inaki.ipgeoapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.google.gson.Gson
import com.inaki.ipgeoapp.local.LocalRepository
import com.inaki.ipgeoapp.local.LocalRepositoryImpl
import com.inaki.ipgeoapp.local.LocationDAO
import com.inaki.ipgeoapp.local.LocationDatabase
import com.inaki.ipgeoapp.rest.CacheInterceptor
import com.inaki.ipgeoapp.rest.IPService
import com.inaki.ipgeoapp.rest.RemoteLocationRepository
import com.inaki.ipgeoapp.rest.RemoteLocationRepositoryImpl
import com.inaki.ipgeoapp.utils.BASE_URL
import com.inaki.ipgeoapp.utils.cacheSize
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideApiService(retrofit: Retrofit): IPService =
        retrofit.create(IPService::class.java)

    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @Provides
    fun providesGson(): Gson = Gson()

    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: CacheInterceptor,
        cache: Cache
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(cacheInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun providesCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, cacheSize)
}

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providesConnectivityManager(
        @ApplicationContext app: Context
    ): ConnectivityManager =
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun providesLocationDao(
        @ApplicationContext context: Context
    ): LocationDAO =
        Room.databaseBuilder(
            context,
            LocationDatabase::class.java,
            "location-db"
        ).build().getLocationDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun provideLocalRepository(repo: LocalRepositoryImpl): LocalRepository

    @Binds
    abstract fun provideRemoteRepository(repo: RemoteLocationRepositoryImpl): RemoteLocationRepository
}