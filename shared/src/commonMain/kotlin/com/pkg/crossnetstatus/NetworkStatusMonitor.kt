package com.pkg.crossnetstatus

expect class NetworkStatusMonitor(
    onStatusChanged: (Boolean) -> Unit
) {
    fun startMonitoring()
    fun stopMonitoring()
}