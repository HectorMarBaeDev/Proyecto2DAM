package com.pokemon.pokemonbackend.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("uploads");

    // Guardar archivo
    public void store(MultipartFile file, String filename) {
        try {
            Files.createDirectories(rootLocation);

            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                    .normalize()
                    .toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo: " + e.getMessage(), e);
        }
    }

    // Cargar un archivo como Path
    public Path load(String filename) {
        return rootLocation.resolve(filename).normalize();
    }

    // Cargar un archivo como Resource
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            throw new RuntimeException("Error cargando archivo: " + filename, e);
        }
    }

    // 🔹 LISTAR TODOS LOS ARCHIVOS
    public Stream<Path> loadAll() {
        try {
            Files.createDirectories(rootLocation);
            return Files.list(rootLocation); // devuelve Stream<Path>
        } catch (IOException e) {
            throw new RuntimeException("No se pueden listar los archivos", e);
        }
    }
}
