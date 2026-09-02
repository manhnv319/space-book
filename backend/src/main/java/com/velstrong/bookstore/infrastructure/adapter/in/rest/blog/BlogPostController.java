package com.velstrong.bookstore.infrastructure.adapter.in.rest.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostResponse;
import com.velstrong.bookstore.application.response.blog.BlogPostSummaryResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import com.velstrong.bookstore.domain.port.in.blog.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * D15 rule #5: list/detail are ONE route each for guests and admins alike —
 * {@code canManage} is read from the caller's authorities (populated by
 * JwtAuthFilter even on public routes when a valid token is present) and
 * enforcement happens in the application services, not in routing.
 */
@RestController
@RequestMapping("/api/v1/blog-posts")
public class BlogPostController {

    private static final String MANAGE_PERMISSION = "book:manage";

    private final GetBlogPostsUseCase getBlogPostsUseCase;
    private final GetBlogPostBySlugUseCase getBlogPostBySlugUseCase;
    private final CreateBlogPostUseCase createBlogPostUseCase;
    private final UpdateBlogPostUseCase updateBlogPostUseCase;
    private final DeleteBlogPostUseCase deleteBlogPostUseCase;
    private final ChangeBlogPostStatusUseCase changeBlogPostStatusUseCase;

    public BlogPostController(GetBlogPostsUseCase getBlogPostsUseCase,
                              GetBlogPostBySlugUseCase getBlogPostBySlugUseCase,
                              CreateBlogPostUseCase createBlogPostUseCase,
                              UpdateBlogPostUseCase updateBlogPostUseCase,
                              DeleteBlogPostUseCase deleteBlogPostUseCase,
                              ChangeBlogPostStatusUseCase changeBlogPostStatusUseCase) {
        this.getBlogPostsUseCase = getBlogPostsUseCase;
        this.getBlogPostBySlugUseCase = getBlogPostBySlugUseCase;
        this.createBlogPostUseCase = createBlogPostUseCase;
        this.updateBlogPostUseCase = updateBlogPostUseCase;
        this.deleteBlogPostUseCase = deleteBlogPostUseCase;
        this.changeBlogPostStatusUseCase = changeBlogPostStatusUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BlogPostSummaryResponse>>> getBlogPosts(
            @RequestParam(required = false) BlogPostStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getBlogPostsUseCase.getBlogPosts(status, hasManagePermission(), page, size)));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
                getBlogPostBySlugUseCase.getBySlug(slug, hasManagePermission())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BlogPostResponse>> create(@RequestAttribute Long currentUserId,
                                                                 @Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                createBlogPostUseCase.create(request.toCreateCommand(currentUserId))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateBlogPostUseCase.update(id, request.toUpdateCommand(id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteBlogPostUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Blog post deleted", null));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<BlogPostResponse>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(changeBlogPostStatusUseCase.publish(id)));
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<BlogPostResponse>> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(changeBlogPostStatusUseCase.unpublish(id)));
    }

    private boolean hasManagePermission() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> MANAGE_PERMISSION.equals(authority.getAuthority()));
    }
}
