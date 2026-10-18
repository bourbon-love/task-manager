package com.example.task_manager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger UI) configuration.
 * Sets API metadata, tag order, and the JWT Bearer scheme so Swagger UI shows an Authorize button.
 */
@Configuration
public class OpenApiConfig {

    /** Tag name for authentication endpoints. */
    public static final String TAG_AUTH = "認証";

    /** Tag name for task endpoints. */
    public static final String TAG_TASKS = "タスク";

    /** Tag name for the health check endpoint. */
    public static final String TAG_HEALTH = "ヘルスチェック";

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0")
                        .description("JWT認証付きのタスク管理REST API。"
                                + "`/api/auth/login` で取得したトークンを「Authorize」に設定すると、タスクAPIを実行できます。"))
                .addTagsItem(new Tag().name(TAG_AUTH).description("ユーザー登録・ログイン（JWT発行）"))
                .addTagsItem(new Tag().name(TAG_TASKS).description("自分のタスクの作成・取得・更新・削除"))
                .addTagsItem(new Tag().name(TAG_HEALTH).description("動作確認"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
