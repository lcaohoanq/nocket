package com.lcaohoanq.nocket.domain.asset

import lombok.extern.slf4j.Slf4j
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Slf4j
@RequestMapping("\${api.prefix}/assets")
@RestController
class AssetController(
    private val assetConfig: AssetConfig
) {
    @GetMapping("/images/{filename:.+}")
    @PreAuthorize("permitAll()")
    @Throws(IOException::class)
    fun serveImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val filePath = Paths.get(assetConfig.imageDirectory).resolve(filename).normalize()
        val resource: Resource = UrlResource(filePath.toUri())

        if (resource.exists() && resource.isReadable) {
            // Automatically determine the content type
            var contentType = Files.probeContentType(filePath)
            if (contentType == null) {
                contentType = "application/octet-stream" // Fallback to binary stream if unknown
            }

            val headers = HttpHeaders()
            headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"$filename\""
            )

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .headers(headers)
                .body(resource)
        } else {
            throw IOException("File not found: $filename")
        }
    }
}
