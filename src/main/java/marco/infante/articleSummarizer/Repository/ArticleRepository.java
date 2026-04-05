package marco.infante.articleSummarizer.Repository;

import marco.infante.articleSummarizer.Model.ArticleSummary;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleRepository extends ReactiveCrudRepository<ArticleSummary, Long> {
    Flux<ArticleSummary> findByStatus(String status);
    Flux<ArticleSummary> findByLanguage(String language);
}
