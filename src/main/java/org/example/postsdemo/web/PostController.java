package org.example.postsdemo.web;

import jakarta.validation.constraints.Min;
import org.example.postsdemo.domain.PostEntity;
import org.example.postsdemo.domain.PostRepository;
import org.example.postsdemo.service.PostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {
    private final PostService postService;
    private final PostRepository postRepository;

    public PostController(PostService postService, PostRepository postRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
    }

    @PostMapping("/sync")
    public List<PostEntity> syncByUser(
            @RequestParam @Min(1) long userId
    ) {
        return postService.fetchAndSaveByUserId(userId);
    }

    @GetMapping
    // GET posts?id=123
    public List<PostEntity> getSaved(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, name = "titleContains") String titleContains
    ) {
        if (userId != null) return postRepository.findByUserId(userId);
        if (titleContains != null && !titleContains.isBlank()) return postRepository.findByTitleContainingIgnoreCase(titleContains);

        return postRepository.findAll();
    }
}