package ddperson.config;

import ddperson.security.CookieNames;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String COOKIE_AUTH = "cookieAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DD Person API")
                        .description("Генерация портретов D&D. JWT передаётся в HttpOnly cookie `access_token`.")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(COOKIE_AUTH))
                .components(new Components().addSecuritySchemes(COOKIE_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name(CookieNames.ACCESS_TOKEN)
                        .description("JWT access token (устанавливается при login/register через Set-Cookie)")));
    }
}
