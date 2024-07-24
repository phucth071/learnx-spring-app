package com.hcmute.utezbe.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCommentDto {
    private String content;
    private Long topicId;
    private Long accountId;
}
