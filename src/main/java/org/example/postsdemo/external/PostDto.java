package org.example.postsdemo.external;

import lombok.Data;

@Data
public class PostDto {
    private Long userId;
    private Long id;
    private String title;
    private String body;
}