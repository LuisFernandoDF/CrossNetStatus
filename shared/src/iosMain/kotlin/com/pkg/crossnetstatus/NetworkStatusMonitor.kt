package com.pkg.crossnetstatus

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

actual class NetworkStatusMonitor actual constructor(
    private val onStatusChanged: (Boolean) -> Unit
) {
    @OptIn(ExperimentalForeignApi::class)
    private var monitor: nw_path_monitor_t? = null


    actual fun startMonitoring() {
        monitor = nw_path_monitor_create()

        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())

        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            val isOnline = (status == nw_path_status_satisfied)
            onStatusChanged(isOnline)
        }

        nw_path_monitor_start(monitor)
    }

    actual fun stopMonitoring() {
        if (monitor != null) {
            nw_path_monitor_cancel(monitor)
            monitor = null
        }
    }
}
