package com.tannerbernth.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tannerbernth.web.error.StorageException;
import com.tannerbernth.web.error.StorageFileNotFoundException;

@Service
public class StorageService {

    private final Path location = Paths.get("uploads/img/article");

    public void store(MultipartFile file, String filename) {
        //String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.location.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.location, 1)
                .filter(path -> !path.equals(this.location))
                .map(this.location::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files");
        }

    }

    public Path load(String filename) {
        return location.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }

    public void delete(String file) {
        FileSystemUtils.deleteRecursively(Paths.get(location.toString(),file).toFile());
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(location.toFile());
    }
}