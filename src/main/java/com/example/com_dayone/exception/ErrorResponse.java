package com.example.com_dayone.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private int code;

    private String message;

}
