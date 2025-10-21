package org.example.postsdemo.service;

import org.example.postsdemo.domain.PostEntity;
import org.example.postsdemo.domain.PostRepository;
import org.example.postsdemo.external.PostDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostService {
	private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";
	private final RestClient restClient;
	private final PostRepository postRepository;

	public PostService(PostRepository postRepository, RestClient restClient) {
		this.postRepository = postRepository;
		this.restClient = restClient;
	}

	public List<PostEntity> fetchAndSaveByUserId(Long userId) {
		PostDto[] response = restClient.get()
						.uri(uriBuilder -> uriBuilder
										.path("/posts")
										.queryParam("userId", userId)
										.build())
						.retrieve()
						.onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
							throw new IllegalStateException("Client error calling posts API: " + res.getStatusCode());
						})
						.onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
							throw new IllegalStateException("Server error calling posts API: " + res.getStatusCode());
						})
						.body(PostDto[].class);

		List<PostDto> dtos = (response == null) ? List.of() : Arrays.asList(response);

		List<PostDto> filtered = dtos.stream()
						.filter(p -> p.getUserId() != null && p.getUserId().equals(userId))
						.collect(Collectors.toList());

		return upsertPosts(filtered);
	}

	private List<PostEntity> upsertPosts(List<PostDto> dtos) {
		if (dtos == null || dtos.isEmpty()) return List.of();

		List<Long> extIds = dtos.stream()
						.map(PostDto::getId)
						.filter(Objects::nonNull)
						.toList();

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