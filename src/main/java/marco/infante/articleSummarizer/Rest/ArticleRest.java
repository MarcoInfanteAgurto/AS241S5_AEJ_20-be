package marco.infante.articleSummarizer.Rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import marco.infante.articleSummarizer.Model.ArticleRequest;
import marco.infante.articleSummarizer.Model.ArticleSummary;
import marco.infante.articleSummarizer.Service.ArticleService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "Article Summarizer", description = "API para resumir artículos con IA")
public class ArticleRest {

    private final ArticleService service;

    public ArticleRest(ArticleService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Resumir artículo", description = "Extrae y resume un artículo dado su URL")
    public Mono<ArticleSummary> summarize(@RequestBody ArticleRequest request) {
        return service.summarize(request);
    }

    @GetMapping
    @Operation(summary = "Listar todos los resúmenes")
    public Flux<ArticleSummary> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID")
    public Mono<ArticleSummary> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Filtrar por estado")
    public Flux<ArticleSummary> getByStatus(@PathVariable String status) {
        return service.getByStatus(status);
    }

    @GetMapping("/lang/{lang}")
    @Operation(summary = "Filtrar por idioma")
    public Flux<ArticleSummary> getByLanguage(@PathVariable String lang) {
        return service.getByLanguage(lang);
    }
}
