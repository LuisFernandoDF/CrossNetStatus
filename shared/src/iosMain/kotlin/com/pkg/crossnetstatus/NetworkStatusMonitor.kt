package com.pkg.crossnetstatus
import platform.Foundation.NSLog
import platform.Network.*
import platform.darwin.*

/**
 * A platform-specific implementation of network status monitoring for iOS.
 * This class utilizes Network.framework to detect changes in network connectivity.
 *
 * @property onStatusChanged A callback function invoked when the network status changes.
 * Receives a Boolean indicating whether the device is online (true) or offline (false).
 */
actual class NetworkStatusMonitor actual constructor(
    private val onStatusChanged: (Boolean) -> Unit
) {
    private var monitor: nw_path_monitor_t? = null

    /**
     * Starts monitoring the network connection status.
     *
     * - If the monitor is already running, this function does nothing.
     * - The network status is evaluated using nw_path_monitor_set_update_handler,
     *   and the callback is triggered when a status change is detected.
     */
    actual fun startMonitoring() {
        if (monitor != null) {
            return
        } // Prevent reinitialization if already running

        monitor = nw_path_monitor_create()?.also { nwMonitor ->
            // Use the main queue instead of a custom queue:
            nw_path_monitor_set_queue(nwMonitor, dispatch_get_main_queue())

            nw_path_monitor_set_update_handler(nwMonitor) { path ->
                val status = nw_path_get_status(path)
                NSLog("nw_path_get_status(path) returns: %d", status)


                val isOnline = status == nw_path_status_satisfied
                NSLog("isOnline from library: %d", isOnline)

                onStatusChanged(isOnline)
            }

            nw_path_monitor_start(nwMonitor)
        }
    }


    /**
     * Stops monitoring the network connection.
     *
     * - Cancels the network monitor to free system resources.
     * - Resets the monitor instance to prevent memory leaks.
     */
    actual fun stopMonitoring() {
        monitor?.let { nwMonitor ->
            nw_path_monitor_cancel(nwMonitor)
            monitor = null
        }
    }
}