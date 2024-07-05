package com.hcmute.utezbe.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    private boolean success;
    private boolean error;
    private String message;
    private Object data;
}

