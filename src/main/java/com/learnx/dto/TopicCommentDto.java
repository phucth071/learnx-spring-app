package com.learnx.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCommentDto {
    private String content;
    private Long topicId;
}
