package marco.infante.imagenGenerator.Rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import marco.infante.imagenGenerator.Model.ImageRecord;
import marco.infante.imagenGenerator.Model.ImageRequest;
import marco.infante.imagenGenerator.Service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Generator", description = "API para generar imágenes con IA")
public class ImageRest {

    private final ImageService imageService;

    @Autowired
    public ImageRest(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    @Operation(summary = "Generar imagen con IA", description = "Crea una imagen a partir de texto usando Stable Diffusion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen generada exitosamente", 
                   content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, 
                              schema = @Schema(implementation = ImageRecord.class))),
        @ApiResponse(responseCode = "400", description = "Request inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ImageRecord> generateImage(@RequestBody ImageRequest request) {
        return imageService.generateImage(request);
    }

    @GetMapping
    @Operation(summary = "Listar todas las imágenes", description = "Retorna todas las imágenes generadas")
    public Flux<ImageRecord> getAll() {
        return imageService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar imagen por ID", description = "Retorna una imagen específica por su ID")
    @Parameter(name = "id", description = "ID de la imagen", required = true)
    public Mono<ImageRecord> getById(@PathVariable String id) {
        return imageService.getById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar por estado", description = "Retorna imágenes filtradas por estado")
    @Parameter(name = "status", description = "Estado a filtrar (generated/failed)", required = true)
    public Flux<ImageRecord> getByStatus(@PathVariable String status) {
        return imageService.getByStatus(status);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar por prompt", description = "Busca imágenes que contengan el keyword en el prompt")
    @Parameter(name = "keyword", description = "Palabra clave a buscar", required = true)
    public Flux<ImageRecord> searchByPrompt(@RequestParam String keyword) {
        return imageService.searchByPrompt(keyword);
    }
}
