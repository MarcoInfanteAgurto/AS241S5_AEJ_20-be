package marco.infante.imagenGenerator.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitud de generación de imagen con Flux")
public class ImageRequest {

    @Schema(
            description = "Descripción de la imagen a generar",
            example = "iron man and spider man",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String prompt;

    @Schema(
            description = "ID del estilo artístico (1-10)",
            example = "4",
            defaultValue = "4"
    )
    private Integer style_id;

    @Schema(
            description = "Tamaño de la imagen: 1-1 (cuadrado), 16-9 (landscape), 9-16 (portrait)",
            example = "1-1",
            defaultValue = "1-1"
    )
    private String size;
}