package com.lcaohoanq.nocket.util

import java.util.*

interface Identifiable {
    fun isMobileDevice(userAgent: String?): Boolean {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        if (userAgent == null) {
            return false
        }
        return userAgent.lowercase(Locale.getDefault()).contains("mobile")
    }
}
