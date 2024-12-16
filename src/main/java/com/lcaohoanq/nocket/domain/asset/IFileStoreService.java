package com.lcaohoanq.nocket.domain.asset;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStoreService {

    String storeFile(MultipartFile file) throws IOException;
    
    MultipartFile validateProductImage(MultipartFile file) throws IOException;
}
