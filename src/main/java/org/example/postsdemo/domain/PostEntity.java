package org.example.postsdemo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_posts_external_id", columnNames = "external_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 500)
    private String title;

    @Column(length = 500)
    private String body;
}