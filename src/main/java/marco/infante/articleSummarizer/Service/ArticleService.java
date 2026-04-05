package marco.infante.articleSummarizer.Service;

import marco.infante.articleSummarizer.Model.ArticleRequest;
import marco.infante.articleSummarizer.Model.ArticleSummary;
import marco.infante.articleSummarizer.Repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ArticleService {

    private final WebClient webClient;
    private final ArticleRepository repository;

    public ArticleService(WebClient webClient, ArticleRepository repository) {
        this.webClient = webClient;
        this.repository = repository;
    }

    public Mono<ArticleSummary> summarize(ArticleRequest request) {

        ArticleSummary record = new ArticleSummary(
                request.getUrl(),
                null,
                request.getLang() != null ? request.getLang() : "es",
                request.getLength() != null ? request.getLength() : 3,
                "pending",
                null,
                LocalDateTime.now()
        );

        // Construir URI con query params
        String uri = UriComponentsBuilder.fromPath("/summarize")
                .queryParam("url", request.getUrl())
                .queryParam("length", request.getLength() != null ? request.getLength() : 3)
                .queryParam("lang", request.getLang() != null ? request.getLang() : "es")
                .queryParam("engine", 2)
                .build()
                .toUriString();

        System.out.println(">>> GET: " + uri);

        return webClient.get()
                .uri(uri)
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
                .bodyToMono(java.util.Map.class)
                .map(response -> {
                    System.out.println(">>> Respuesta: " + response);

                    if (response.containsKey("summary")) {
                        record.setSummary(response.get("summary").toString());
                        record.setStatus("generated");
                        System.out.println("✅ Resumen generado");
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

    public Flux<ArticleSummary> getAll() { return repository.findAll(); }
    public Mono<ArticleSummary> getById(Long id) { return repository.findById(id); }
    public Flux<ArticleSummary> getByStatus(String status) { return repository.findByStatus(status); }
    public Flux<ArticleSummary> getByLanguage(String lang) { return repository.findByLanguage(lang); }
}
