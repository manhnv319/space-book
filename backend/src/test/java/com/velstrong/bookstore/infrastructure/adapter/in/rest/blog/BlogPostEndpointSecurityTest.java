package com.velstrong.bookstore.infrastructure.adapter.in.rest.blog;

import com.velstrong.bookstore.application.command.blog.CreateBlogPostCommand;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.in.blog.ChangeBlogPostStatusUseCase;
import com.velstrong.bookstore.domain.port.in.blog.CreateBlogPostUseCase;
import com.velstrong.bookstore.domain.port.out.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * D15 rule #5: GET /blog-posts and GET /blog-posts/{slug} are single routes
 * shared by guests and admins — enforcement happens in the application
 * service (GetBlogPostsService / GetBlogPostBySlugService), not in routing.
 * These tests prove that end to end through the real security filter chain.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BlogPostEndpointSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CreateBlogPostUseCase createBlogPostUseCase;

    @Autowired
    private ChangeBlogPostStatusUseCase changeBlogPostStatusUseCase;

    @Test
    void guestListIgnoresDraftStatusQueryParamAndNeverReturnsDraftItems() throws Exception {
        // control: guarantee at least one DRAFT row exists right now
        createBlogPostUseCase.create(new CreateBlogPostCommand(
                "Draft control " + System.nanoTime(), null, null, "content", null, null, 1L));

        mockMvc.perform(get("/api/v1/blog-posts").param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("\"status\":\"DRAFT\""))));
    }

    @Test
    void guestDetailByDraftSlugIsNotFound() throws Exception {
        var draft = createBlogPostUseCase.create(new CreateBlogPostCommand(
                "Draft only " + System.nanoTime(), null, null, "content", null, null, 1L));

        mockMvc.perform(get("/api/v1/blog-posts/" + draft.slug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void guestDetailByPublishedSlugSucceeds() throws Exception {
        var post = createBlogPostUseCase.create(new CreateBlogPostCommand(
                "Live post " + System.nanoTime(), null, null, "content", null, null, 1L));
        changeBlogPostStatusUseCase.publish(post.id());

        mockMvc.perform(get("/api/v1/blog-posts/" + post.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    void createRejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/blog-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\",\"content\":\"y\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRejectsUserWithoutBookManagePermission() throws Exception {
        String token = jwtService.generateAccessToken(userWithScopes(List.of()));

        mockMvc.perform(post("/api/v1/blog-posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\",\"content\":\"y\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSucceedsForAuthorizedUserAndStartsAsDraft() throws Exception {
        String token = jwtService.generateAccessToken(userWithScopes(List.of("book:manage")));
        String body = String.format("{\"title\":\"%s\",\"content\":\"content\"}", "Http created " + System.nanoTime());

        mockMvc.perform(post("/api/v1/blog-posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void writeEndpointsRejectRequestsWithoutToken() throws Exception {
        mockMvc.perform(put("/api/v1/blog-posts/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\",\"content\":\"y\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/blog-posts/999999"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/blog-posts/999999/publish"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/blog-posts/999999/unpublish"))
                .andExpect(status().isForbidden());
    }

    private User userWithScopes(List<String> scopes) {
        return User.reconstitute(999999L, "blog-security-test", "hash", "blog-security-test@example.com",
                "Blog Security Test", null, null, null, (byte) 1, UserStatus.ACTIVE,
                List.of("CUSTOMER"), scopes);
    }
}
