package com.example.androidconcepts.hilt.module

import com.example.androidconcepts.common.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    @Provides
    @Singleton
    fun providesOkhttpClient() : OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun providesRetrofitClient(okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }
}