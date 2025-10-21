package org.example.postsdemo.service;

import org.example.postsdemo.domain.PostEntity;
import org.example.postsdemo.domain.PostRepository;
import org.example.postsdemo.external.PostDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";
    private final RestTemplate restTemplate;
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository, RestTemplate restTemplate) {
        this.postRepository = postRepository;
        this.restTemplate = restTemplate;
    }

    public List<PostEntity> fetchAndSaveByUserId(Long userId) {
        String url = POSTS_URL + "?userId=" + userId;

        PostDto[] response = restTemplate.getForObject(url, PostDto[].class);
        List<PostDto> dtos = response == null ? List.of() : Arrays.asList(response);

        // Defensive
        List<PostDto> filtered = dtos.stream()
                .filter(p -> p.getUserId() != null && p.getUserId().equals(userId))
                .collect(Collectors.toList());

        return upsertPosts(filtered); // UPDATE OR INSERT -> ESTA?
                                                            // SI -> UPDATE
                                                            // NO -> INSERT
    }

    private List<PostEntity> upsertPosts(List<PostDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();

        List<Long> extIds = dtos.stream().map(PostDto::getId).filter(Objects::nonNull).toList();
        Map<Long, PostEntity> existingByExtId = postRepository.findByExternalIdIn(extIds).stream()
                .collect(Collectors.toMap(PostEntity::getExternalId, Function.identity()));

        List<PostEntity> toSave = new ArrayList<>();
        for (PostDto dto : dtos) {
            if (dto.getId() == null) continue;

            PostEntity entity = existingByExtId.getOrDefault(dto.getId(), new PostEntity());
            entity.setExternalId(dto.getId());
            entity.setUserId(dto.getUserId());
            entity.setTitle(dto.getTitle());
            entity.setBody(dto.getBody());

            toSave.add(entity);
        }

        return postRepository.saveAll(toSave);
    }
}