package com.example.testapp.di

import com.example.testapp.BuildConfig
import com.example.testapp.model.Constants.TIME_OUT
import com.example.testapp.network.CDRApi
import com.example.testapp.repository.CdrRepository
import com.example.testapp.ui.MainVM
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val vmModule = module{
    viewModel{MainVM(androidApplication(),get())}
}

val networkModule = module{
    single{ provideOkhttpClient()}
    single{ provideRetrofit(get())}
    factory { provideCdrApi(get()) }
    factory { CdrRepository(get()) }
}

private fun provideCdrApi(retrofit: Retrofit) = retrofit.create(CDRApi::class.java)

private fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

private fun provideOkhttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    val builder = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
        .writeTimeout(TIME_OUT, TimeUnit.MINUTES)
        .readTimeout(TIME_OUT, TimeUnit.MINUTES)
        .addInterceptor(loggingInterceptor)
    return builder.build()
}