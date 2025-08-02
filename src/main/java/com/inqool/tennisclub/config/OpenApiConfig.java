package com.inqool.tennisclub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Tennis Club API", version = "1.0", description = "Tennis Club Management API"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components().addSchemas("ErrorResponse", createErrorResponseSchema()));
    }

    private Schema<?> createErrorResponseSchema() {
        Schema<?> errorSchema = new Schema<>();
        errorSchema.type("object");
        errorSchema.addProperty("error", new Schema<>().type("string").example("Unauthorized"));
        errorSchema.addProperty("message", new Schema<>().type("string").example("Authentication required"));

        errorSchema.example(Map.of(
                "error", "Unauthorized",
                "message", "Authentication required"));

        return errorSchema;
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getSecurity() != null
                            && !operation.getSecurity().isEmpty()) {
                        operation
                                .getResponses()
                                .addApiResponse("401", createErrorResponse("Unauthorized", "Authentication required"));

                        operation
                                .getResponses()
                                .addApiResponse("403", createErrorResponse("Forbidden", "Access Denied"));
                    }
                });
            });
        };
    }

    private ApiResponse createErrorResponse(String error, String message) {
        Schema<?> responseSchema = new Schema<>();
        responseSchema.type("object");
        responseSchema.addProperty("error", new Schema<>().type("string"));
        responseSchema.addProperty("message", new Schema<>().type("string"));
        responseSchema.example(Map.of("error", error, "message", message));

        MediaType mediaType = new MediaType();
        mediaType.schema(responseSchema);

        Content content = new Content();
        content.addMediaType("application/json", mediaType);

        return new ApiResponse().description(error).content(content);
    }
}
