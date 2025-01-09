package com.lcaohoanq.nocket.domain.asset

import org.springframework.web.multipart.MultipartFile
import java.io.IOException

interface IFileStoreService {
    @Throws(IOException::class)
    fun storeFile(file: MultipartFile): String

    @Throws(IOException::class)
    fun validateProductImage(file: MultipartFile): MultipartFile
}
