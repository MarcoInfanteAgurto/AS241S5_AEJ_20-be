package marco.infante.imagenGenerator.Repository;

import marco.infante.imagenGenerator.Model.ImageRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ImageRepository extends ReactiveMongoRepository<ImageRecord, String> {
    Flux<ImageRecord> findByStatus(String status);
    Flux<ImageRecord> findByPromptContainingIgnoreCase(String keyword);
}