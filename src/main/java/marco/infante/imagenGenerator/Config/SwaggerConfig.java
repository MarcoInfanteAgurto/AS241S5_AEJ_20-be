package marco.infante.imagenGenerator.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stable Diffusion Image Generator API")
                        .version("2.0.0")
                        .description("Backend reactivo para generar imágenes con IA usando Stable Diffusion API + MongoDB")
                        .contact(new Contact()
                                .name("Marco Infante")
                                .email("marco.infante@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://tu-api-production.com")
                                .description("Servidor de producción")
                ));
    }
}