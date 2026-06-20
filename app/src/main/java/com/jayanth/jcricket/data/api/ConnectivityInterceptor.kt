package com.jayanth.jcricket.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.net.SocketTimeoutException

class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectException -> {
                    throw IOException("No internet connection. Please check your network settings and try again.", e)
                }
                is SocketTimeoutException -> {
                    throw IOException("Connection timed out. Please try again.", e)
                }
                else -> throw e
            }
        }
    }
}
