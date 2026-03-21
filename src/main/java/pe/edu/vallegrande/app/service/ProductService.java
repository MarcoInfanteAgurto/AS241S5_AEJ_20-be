package pe.edu.vallegrande.app.service;

import pe.edu.vallegrande.app.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> findAll();

    Mono<Product> findById(Long id);

    Mono<Product> save(Product product);

    Mono<Product> update(Product product);

    Mono<Product> activate(String id);

    Mono<Product> inactivate(String id);
  
}
