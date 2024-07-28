package rw.companyz.useraccountms.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.fileHandling.File;

import java.io.IOException;
import java.util.UUID;


public interface IFileService {
    File create(MultipartFile document) throws Exception;
    File getById(UUID id) throws ResourceNotFoundException;

    File findByName(String name) throws ResourceNotFoundException;

    void deleteById(UUID id) throws ResourceNotFoundException;

    Resource load(String filePath) throws IOException;
}

