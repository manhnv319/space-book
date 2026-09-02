package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import com.velstrong.bookstore.infrastructure.config.MediaStorageProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/books")
public class BookCoverUploadController {
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");
    private final BookRepository books;
    private final MediaStorageProperties media;

    public BookCoverUploadController(BookRepository books, MediaStorageProperties media) { this.books = books; this.media = media; }

    @PostMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
        books.findById(id).orElseThrow(() -> new EntityNotFoundException("Book", id));
        if (file.isEmpty() || file.getSize() > media.maxBytes() || !ALLOWED.contains(file.getContentType()))
            throw new InvalidOperationException("Cover must be a JPEG, PNG, or WebP image under " + media.maxBytes() + " bytes");
        Path directory = Path.of(media.storagePath(), "books", id.toString());
        Files.createDirectories(directory);
        String token = UUID.randomUUID().toString();
        Path input = Files.createTempFile(directory, "source-", ".upload");
        Path output = directory.resolve("cover-" + token + ".webp");
        try {
            file.transferTo(input);
            Process process = new ProcessBuilder("cwebp", "-quiet", "-q", "82", input.toString(), "-o", output.toString()).start();
            if (!process.waitFor(20, TimeUnit.SECONDS) || process.exitValue() != 0 || !Files.isRegularFile(output))
                throw new InvalidOperationException("Unable to convert cover to WebP");
            String url = "/media/books/" + id + "/" + output.getFileName();
            if (!books.updateImageUrl(id, url)) throw new EntityNotFoundException("Book", id);
            return ResponseEntity.ok(ApiResponse.success(url));
        } finally { Files.deleteIfExists(input); }
    }
}
