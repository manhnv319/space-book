package com.velstrong.bookstore.infrastructure.adapter.out.storage.support;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.SupportAttachmentUpload;
import com.velstrong.bookstore.domain.model.SupportMessageAttachment;
import com.velstrong.bookstore.domain.port.out.SupportAttachmentStorage;
import com.velstrong.bookstore.infrastructure.config.MediaStorageProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LocalSupportAttachmentStorage implements SupportAttachmentStorage {
    private static final int MAX_ATTACHMENTS = 3;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");
    private final MediaStorageProperties media;

    public LocalSupportAttachmentStorage(MediaStorageProperties media) {
        this.media = media;
    }

    @Override
    public List<SupportMessageAttachment> store(Long conversationId, Long messageId, List<SupportAttachmentUpload> uploads) {
        if (uploads.isEmpty()) return List.of();
        if (uploads.size() > MAX_ATTACHMENTS) throw new InvalidOperationException("A message can include at most 3 images");
        List<Path> written = new ArrayList<>();
        try {
            Path directory = Path.of(media.storagePath(), "support", conversationId.toString(), messageId.toString());
            Files.createDirectories(directory);
            List<SupportMessageAttachment> result = new ArrayList<>();
            for (SupportAttachmentUpload upload : uploads) {
                validate(upload);
                Path output = directory.resolve("image-" + UUID.randomUUID() + ".webp");
                convert(upload.content(), directory, output);
                written.add(output);
                result.add(new SupportMessageAttachment(null, messageId, "/media/support/" + conversationId + "/" + messageId + "/" + output.getFileName(),
                        cleanName(upload.originalName()), "image/webp", LocalDateTime.now()));
            }
            return result;
        } catch (InvalidOperationException exception) {
            deleteQuietly(written);
            throw exception;
        } catch (Exception exception) {
            deleteQuietly(written);
            throw new InvalidOperationException("Unable to store support image");
        }
    }

    private void validate(SupportAttachmentUpload upload) {
        if (!ALLOWED.contains(upload.contentType()) || upload.content().length == 0 || upload.content().length > media.maxBytes())
            throw new InvalidOperationException("Support image must be JPEG, PNG, or WebP under " + media.maxBytes() + " bytes");
    }

    private void convert(byte[] content, Path directory, Path output) throws Exception {
        Path input = Files.createTempFile(directory, "source-", ".upload");
        try {
            Files.write(input, content);
            Process process = new ProcessBuilder("cwebp", "-quiet", "-q", "82", input.toString(), "-o", output.toString()).start();
            if (!process.waitFor(20, TimeUnit.SECONDS) || process.exitValue() != 0 || !Files.isRegularFile(output))
                throw new InvalidOperationException("Unable to convert support image to WebP");
        } finally {
            Files.deleteIfExists(input);
        }
    }

    private static String cleanName(String value) {
        String source = value == null ? "image" : Path.of(value).getFileName().toString().replaceAll("[^A-Za-z0-9._ -]", "_");
        return source.isBlank() ? "image" : source.substring(0, Math.min(source.length(), 255));
    }

    private static void deleteQuietly(List<Path> files) {
        for (Path file : files) try { Files.deleteIfExists(file); } catch (Exception ignored) { }
    }
}
