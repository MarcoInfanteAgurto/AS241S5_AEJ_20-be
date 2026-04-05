package marco.infante.imagenGenerator.Service;

import marco.infante.imagenGenerator.Model.ImageRecord;
import marco.infante.imagenGenerator.Model.ImageRequest;
import marco.infante.imagenGenerator.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {

    private final WebClient webClient;
    private final ImageRepository repository;
    private final String endpoint;

    @Autowired
    public ImageService(WebClient webClient, ImageRepository repository,
                        @Value("${rapidapi.endpoint-stable-diffusion}") String endpoint) {
        this.webClient = webClient;
        this.repository = repository;
        this.endpoint = endpoint;
    }

    public Mono<ImageRecord> generateImage(ImageRequest request) {

        ImageRecord record = new ImageRecord(
                request.getPrompt(),
                null,
                request.getStyle_id() != null ? request.getStyle_id() : 4,
                request.getSize() != null ? request.getSize() : "1-1",
                "pending",
                null,
                LocalDateTime.now()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("prompt", request.getPrompt());
        body.put("style_id", request.getStyle_id() != null ? request.getStyle_id() : 4);
        body.put("size", request.getSize() != null ? request.getSize() : "1-1");

        System.out.println(">>> POST: " + endpoint);
        System.out.println(">>> Body: " + body);

        return webClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(err -> {
                                    System.err.println(">>> 4xx: " + err);
                                    return Mono.error(new RuntimeException("Error 4xx: " + err));
                                })
                )
                .onStatus(status -> status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(err -> {
                                    System.err.println(">>> 5xx: " + err);
                                    return Mono.error(new RuntimeException("Error 5xx: " + err));
                                })
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    System.out.println(">>> Respuesta: " + response);

                    if (response.containsKey("final_result")) {
                        // Extraer la lista de imágenes
                        @SuppressWarnings("unchecked")
                        java.util.List<java.util.Map<String, Object>> results =
                                (java.util.List<java.util.Map<String, Object>>) response.get("final_result");

                        if (results != null && !results.isEmpty()) {
                            // Tomar la primera imagen
                            record.setImageUrl(results.get(0).get("origin").toString());
                            if (results.size() > 1) {
                                record.setImageUrl2(results.get(1).get("origin").toString());
                            }
                            record.setStatus("generated");
                            System.out.println("✅ Imagen generada: " + results.get(0).get("origin"));
                        } else {
                            record.setStatus("failed");
                            record.setErrorMessage("Lista de imágenes vacía");
                        }
                    } else {
                        record.setStatus("failed");
                        record.setErrorMessage("Respuesta inesperada: " + response);
                    }
                    return record;
                })
                .flatMap(repository::save)
                .onErrorResume(e -> {
                    System.err.println(">>> ERROR: " + e.getMessage());
                    record.setStatus("failed");
                    record.setErrorMessage(e.getMessage());
                    return repository.save(record);
                });
    }

    public Flux<ImageRecord> getAll() { return repository.findAll(); }

    public Mono<ImageRecord> getById(String id) { return repository.findById(id); }

    public Flux<ImageRecord> getByStatus(String status) { return repository.findByStatus(status); }

    public Flux<ImageRecord> searchByPrompt(String keyword) {
        return repository.findByPromptContainingIgnoreCase(keyword);
    }
}