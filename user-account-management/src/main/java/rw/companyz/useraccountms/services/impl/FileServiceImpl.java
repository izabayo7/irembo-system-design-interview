package rw.companyz.useraccountms.services.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.fileHandling.File;
import rw.companyz.useraccountms.fileHandling.FileStorageService;
import rw.companyz.useraccountms.models.enums.EFileSizeType;
import rw.companyz.useraccountms.models.enums.EFileStatus;
import rw.companyz.useraccountms.repositories.IFileRepository;
import rw.companyz.useraccountms.services.IFileService;
import rw.companyz.useraccountms.utils.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

    private final Logger log = (Logger) LoggerFactory.getLogger(FileServiceImpl.class);

    private final IFileRepository fileRepository;

    private final FileStorageService fileStorageService;

    public FileServiceImpl(IFileRepository fileRepository, FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public File findByName(String name) throws ResourceNotFoundException {
        Optional<File> fileOptional = fileRepository.findByName(name);
        if (fileOptional.isEmpty())
            throw new ResourceNotFoundException("File", "name", name);
        return fileOptional.get();
    }


    public File create(MultipartFile document) throws Exception {
        File file = new File();

        String fileName = FileUtil.generateUUID(Objects.requireNonNull(document.getOriginalFilename()));
        String documentSizeType = FileUtil.getFileSizeTypeFromFileSize(file.getSize());
        int documentSize = FileUtil.getFormattedFileSizeFromFileSize(file.getSize(), EFileSizeType.valueOf(documentSizeType));

        file.setName(fileName);
        file.setPath(fileStorageService.save(document, fileName));
        file.setStatus(EFileStatus.SAVED);
        file.setType(document.getContentType());

        file.setSize(documentSize);
        file.setSizeType(EFileSizeType.valueOf(documentSizeType));

        return this.fileRepository.save(file);
    }

    @Override
    public File getById(UUID id) throws ResourceNotFoundException {
       return this.fileRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("File", "id", id.toString())
        );
    }
    @Override
    public Resource load(String filePath) throws IOException {
        Path path = Path.of(filePath);
        return new ByteArrayResource(Files.readAllBytes(path));
    }

    @Override
    public void deleteById(UUID id) {
        this.fileRepository.deleteById(id);
    }


}

