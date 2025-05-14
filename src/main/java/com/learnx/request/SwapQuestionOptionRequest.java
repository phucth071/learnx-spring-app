package com.learnx.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapQuestionOptionRequest {
    private Long questionId;
    private Long optionIdSrc;
    private Long optionIdDest;
}
