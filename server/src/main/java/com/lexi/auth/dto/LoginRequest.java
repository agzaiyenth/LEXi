package com.lexi.auth.dto;

import com.lexi.common.exception.GlobalExceptionHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
@Getter
public class LoginRequest {
    private String username;
    private String password;
}
