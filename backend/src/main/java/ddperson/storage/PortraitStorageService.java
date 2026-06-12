package ddperson.storage;

import ddperson.config.AppProperties;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@Service
public class PortraitStorageService {

    private final Path basePath;

    public PortraitStorageService(AppProperties properties) {
        this.basePath = Paths.get(properties.storage().portraitsPath()).toAbsolutePath().normalize();
    }

    public record ImageDimensions(int width, int height) {
    }

    public Optional<ImageDimensions> readJpegDimensions(byte[] imageBytes) {
        try (ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpeg");
            if (!readers.hasNext()) {
                return Optional.empty();
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                return Optional.of(new ImageDimensions(reader.getWidth(0), reader.getHeight(0)));
            } finally {
                reader.dispose();
            }
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    public String save(UUID userId, UUID requestId, byte[] imageBytes) throws IOException {
        Path userDir = basePath.resolve(userId.toString());
        Files.createDirectories(userDir);
        String relative = userId + "/" + requestId + ".jpg";
        Path target = basePath.resolve(relative);
        Files.write(target, imageBytes);
        return relative;
    }

    public byte[] read(String relativePath) throws IOException {
        Path file = basePath.resolve(relativePath).normalize();
        if (!file.startsWith(basePath) || !Files.exists(file)) {
            throw new IOException("Файл не найден: " + relativePath);
        }
        return Files.readAllBytes(file);
    }

    public Path resolve(String relativePath) {
        return basePath.resolve(relativePath).normalize();
    }

    public void deleteIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path file = basePath.resolve(relativePath).normalize();
            if (file.startsWith(basePath)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Не удалось удалить файл портрета: " + relativePath, ex);
        }
    }
}
