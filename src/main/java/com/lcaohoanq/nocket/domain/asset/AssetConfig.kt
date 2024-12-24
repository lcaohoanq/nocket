package com.lcaohoanq.nocket.domain.asset

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class AssetConfig(
    @Value("\${servlet.multipart.location:uploads}")
    val imageDirectory: String = "uploads"
)