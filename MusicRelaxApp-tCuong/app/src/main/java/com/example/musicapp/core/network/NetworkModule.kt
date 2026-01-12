package com.example.musicapp.core.network

import android.content.Context
import android.content.SharedPreferences
import com.example.musicapp.core.common.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module cung cấp dependencies cho Network layer
 * Tạo singleton instances: Retrofit, OkHttp, Gson...
 */
@Module
@InstallIn(SingletonComponent::class) // Scope: App-level singleton
object NetworkModule {
    
    /**
     * Cung cấp SharedPreferences instance
     * Dùng để lưu token, user preferences
     * 
     * @param context: Application context
     * @return SharedPreferences instance
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "music_app_prefs",          // Tên file preferences
            Context.MODE_PRIVATE         // Private mode (chỉ app này access được)
        )
    }
    
    /**
     * Cung cấp Gson instance
     * Parse JSON ↔ Kotlin objects
     * 
     * @return Gson với config tối ưu
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()               // Cho phép JSON format linh hoạt
            .create()
    }
    
    /**
     * Cung cấp Logging Interceptor
     * Log tất cả HTTP request/response (debug only)
     * 
     * VD log:
     * POST /api/login
     * Body: {"email":"test@test.com"}
     * Response: 200 OK {"token":"abc123"}
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log cả body
        }
    }
    
    /**
     * Cung cấp OkHttpClient
     * HTTP client xử lý network requests
     * 
     * Config:
     * - Timeout: 30s
     * - Auto thêm Authorization header
     * - Log requests (debug)
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)      // Thêm token vào mọi request
            .addInterceptor(loggingInterceptor)   // Log requests
            .build()
    }
    
    /**
     * Cung cấp Retrofit instance
     * Main library gọi API
     * 
     * @return Retrofit configured với Gson converter
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Cung cấp ApiService instance
     * Interface chứa tất cả API calls
     * 
     * @return Implementation của ApiService (auto-generated bởi Retrofit)
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}