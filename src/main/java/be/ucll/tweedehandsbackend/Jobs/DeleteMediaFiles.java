package be.ucll.tweedehandsbackend.Jobs;

import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeleteMediaFiles {

    @Value("${storage.location}")
    private String StoragePath;

    @Job
    public void dispatch(String uuid) {
        try {
            Path path = Paths.get(StoragePath, uuid);

            // Check if the folder is empty
            if (Files.list(path).findAny().isPresent()) {
                // For each file in the folder
                Files.list(path).forEach(file -> {
                    try {
                        // Delete the file
                        Files.deleteIfExists(file);
                    } catch (IOException ignored) {}
                });
            }

            // Delete the folder
            Files.deleteIfExists(Paths.get(StoragePath, uuid));
        } catch (IOException ignored) {}
    }

}
