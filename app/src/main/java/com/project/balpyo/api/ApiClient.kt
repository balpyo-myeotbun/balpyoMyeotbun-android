package com.project.balpyo.api

import android.content.Context
import com.project.balpyo.BuildConfig
import com.google.gson.Gson
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ApiClient(val context: Context) {

    val gson = Gson()

    val logger = HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level.BASIC
    }
    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient에 TokenInterceptor를 추가
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(logger)
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()

    val retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.server_url)
            .client(getUnsafeOkHttpClient().build()) //SSL 우회
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService = retrofit.create(ApiService::class.java)


    /**
     * Retrofit SSL 우회 접속 통신
     */
    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { hostname, session -> true }
        builder.addInterceptor(interceptor)
        builder.addInterceptor(logger)
        builder.cookieJar(JavaNetCookieJar(CookieManager()))

        return builder
    }
}

