package com.lcaohoanq.nocket.domain.asset

import com.lcaohoanq.nocket.component.LocalizationUtils
import com.lcaohoanq.nocket.exception.FileTooLargeException
import com.lcaohoanq.nocket.exception.UnsupportedMediaTypeException
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileStoreService(private val localizationUtils: LocalizationUtils) : IFileStoreService {
    @Throws(IOException::class)
    override fun storeFile(file: MultipartFile): String {
        if (!isImageFile(file) || file.originalFilename == null) {
            throw IOException("Invalid image format")
        }
        val filename = StringUtils.cleanPath(Objects.requireNonNull(file.originalFilename))
        val uniqueFileName = UUID.randomUUID().toString() + "_" + filename
        val uploadDir = Paths.get("uploads")
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
        //File.separator: depends on the OS, for windows it is '\', for linux it is '/'
        val destination = Paths.get(uploadDir.toString() + File.separator + uniqueFileName)
        //copy the file to the destination
        Files.copy(file.inputStream, destination, StandardCopyOption.REPLACE_EXISTING)
        return uniqueFileName
    }

    @Throws(IOException::class)
    override fun validateProductImage(file: MultipartFile): MultipartFile {
        // Kiểm tra kích thước file và định dạng
        if (file.size == 0L) {
            throw IOException("File is empty")
        }

        if (file.size > 10 * 1024 * 1024) { // Kích thước > 10MB
            throw FileTooLargeException("File is too large")
        }
        val contentType = file.contentType
        if (contentType == null || !contentType.startsWith("image/")) {
            throw UnsupportedMediaTypeException("Unsupported media type")
        }
        return file
    }

    private fun isImageFile(file: MultipartFile): Boolean {
        val contentType = file.contentType
        return contentType != null && contentType.startsWith("image/")
    }
}
