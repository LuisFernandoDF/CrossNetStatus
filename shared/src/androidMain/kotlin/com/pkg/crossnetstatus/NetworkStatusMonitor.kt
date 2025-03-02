package com.pkg.crossnetstatus

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

actual class NetworkStatusMonitor actual constructor(
    private val onStatusChanged: (Boolean) -> Unit
) {
    private var connectivityManager: ConnectivityManager? = null
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    var globalContext: Context? = null

    fun initialize(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    actual fun startMonitoring() {
        // Si no se inicializó, intenta usar el globalContext
        if (connectivityManager == null) {
            globalContext?.let {
                connectivityManager = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            } ?: throw IllegalStateException("Contexto no proporcionado. Llama a initialize(context) o asigna globalContext.")
        }
        val cm = connectivityManager ?: return

        val request = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onStatusChanged(true)
            }
            override fun onLost(network: Network) {
                onStatusChanged(false)
            }
        }
        cm.registerNetworkCallback(request, networkCallback)
    }

    actual fun stopMonitoring() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
}