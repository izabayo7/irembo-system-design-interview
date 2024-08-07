package rw.companyz.useraccountms.fileHandling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    @Value("${upload.directory}")
    private String root;

    public void init() throws Exception {
        try {
            Files.createDirectory(Paths.get(root));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    public void createSubDirectory(String directory) throws IOException {
        try {
            Files.createDirectories(Paths.get(root + directory));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    public String save(MultipartFile file, String filename) throws Exception {
        try {
            Path of = Path.of(root);
            Files.copy(file.getInputStream(), of.resolve(Objects.requireNonNull(filename)));
            return of.toString() + "/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }



    public Resource load(String uploadDirectory, String fileName) {
        Path path = Paths.get(uploadDirectory);

        try {
            Path file = path.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(root).toFile());
    }


    public Stream<Path> loadAll() throws IOException {
        try {
            return Files.walk(Paths.get(this.root), 1).filter(path -> !path.equals(this.root)).map(Paths.get(this.root)::relativize);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}

