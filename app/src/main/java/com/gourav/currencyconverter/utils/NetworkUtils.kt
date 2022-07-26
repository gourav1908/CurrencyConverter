package com.gourav.currencyconverter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class NetworkUtils {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    ) ?: false
                } else {
                    this.activeNetworkInfo?.isConnected ?: false
                }
            }
        }
    }
}