package rw.companyz.useraccountms.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import rw.companyz.useraccountms.exceptions.*;
import rw.companyz.useraccountms.models.domains.ApiResponse;

import java.io.IOException;

@Configuration
public class FeignConfig {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InternalServerErrorException.class);

    public FeignConfig() {
    }


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
                if (authHeader != null) requestTemplate.header(AUTHORIZATION_HEADER, authHeader);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String message;
            try {
                if (response.body() == null) {
                    message = response.reason();
                }
                else{
                    message = new ObjectMapper().readValue(response.body().asInputStream().readAllBytes(), ApiResponse.class).getMessage();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new InternalServerErrorException();
            }
            return switch (response.status()) {
                case 400 -> new BadRequestException(message);
                case 401 -> new UserLoginException(message);
                case 404 -> new ResourceNotFoundException(message, "FEIGN_ERROR");
                case 500 -> new InternalServerErrorException();
                default -> new Exception(message);
            };
        };
    }
}

