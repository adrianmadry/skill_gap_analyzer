package com.skillgap.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SkillGap Analyzer API",
        version = "1.0.0",
        description = "API for skill gap analysis and IT job market skill matching"
        )
    ,
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Dev Server")
    }
)
public class OpenApiConfig {

}
