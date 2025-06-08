package com.learnx.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
    private String title;
    private String description;
    private int timeLimit;
    private String startDate;
    private String endDate;
    private int attemptAllowed;
    private boolean status;
    private boolean isShuffled;
}
