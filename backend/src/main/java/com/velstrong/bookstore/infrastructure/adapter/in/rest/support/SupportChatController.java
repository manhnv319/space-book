package com.velstrong.bookstore.infrastructure.adapter.in.rest.support;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.support.SupportConversationResponse;
import com.velstrong.bookstore.domain.model.SupportAttachmentUpload;
import com.velstrong.bookstore.domain.port.in.support.SupportChatUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/support")
public class SupportChatController {
    public record SendMessageRequest(@NotBlank @Size(max = 2000) String body) { }
    private final SupportChatUseCase chat;

    public SupportChatController(SupportChatUseCase chat) { this.chat = chat; }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> myConversation(@RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(chat.myConversation(currentUserId)));
    }

    @PostMapping(value = "/conversation/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SupportConversationResponse>> sendJson(@RequestAttribute Long currentUserId,
                                                                               @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chat.sendAsCustomer(currentUserId, request.body(), List.of())));
    }

    @PostMapping(value = "/conversation/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SupportConversationResponse>> send(@RequestAttribute Long currentUserId,
            @RequestParam(required = false) String body, @RequestParam(name = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(chat.sendAsCustomer(currentUserId, body, uploads(attachments))));
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<PagedResponse<SupportConversationResponse>>> queue(@RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(chat.queue(page, size)));
    }

    @GetMapping("/conversations/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(chat.staffUnreadConversationCount()));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> view(@RequestAttribute Long currentUserId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(chat.staffView(id, currentUserId)));
    }

    @PostMapping(value = "/conversations/{id}/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SupportConversationResponse>> replyJson(@RequestAttribute Long currentUserId, @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chat.replyAsStaff(id, currentUserId, request.body(), List.of())));
    }

    @PostMapping(value = "/conversations/{id}/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SupportConversationResponse>> reply(@RequestAttribute Long currentUserId, @PathVariable Long id,
            @RequestParam(required = false) String body, @RequestParam(name = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(chat.replyAsStaff(id, currentUserId, body, uploads(attachments))));
    }

    private static List<SupportAttachmentUpload> uploads(List<MultipartFile> files) throws IOException {
        if (files == null) return List.of();
        return files.stream().filter(file -> !file.isEmpty()).map(file -> {
            try { return new SupportAttachmentUpload(file.getOriginalFilename(), file.getContentType(), file.getBytes()); }
            catch (IOException exception) { throw new SupportUploadReadException(exception); }
        }).toList();
    }

    private static final class SupportUploadReadException extends RuntimeException {
        private SupportUploadReadException(IOException cause) { super(cause); }
    }
}
