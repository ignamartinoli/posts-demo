package org.example.postsdemo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findByExternalId(Long externalId);
    List<PostEntity> findByExternalIdIn(Collection<Long> externalIds);

    List<PostEntity> findByUserId(Long userId);
    List<PostEntity> findByTitleContainingIgnoreCase(String titlePart);
}