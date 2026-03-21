package pe.edu.vallegrande.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.edu.vallegrande.app.model.Product;
import pe.edu.vallegrande.app.repository.ProductRepository;
import pe.edu.vallegrande.app.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Flux<Product> findAll() {
        log.info("Mostrando datos");
        return productRepository.findAll();
    }

    @Override
    public Mono<Product> findById(Long id) {
        log.info("Mostrando datos por ID ");
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        log.info("Registrando datos " + product.toString());
        product.setState("A");
        return productRepository.save(product);
    }

    @Override
    public Mono<Product> update(Product product) {
        log.info("Actualizando datos " + product.toString());
        product.setState("A");
        return productRepository.save(product);
    }

    @Override
    public Mono<Product> activate(String id) {
        log.info("Activando producto con ID: {}", id);
        return productRepository.findById(Long.valueOf(id))
                .flatMap(product -> {
                    product.setState("Active");
                    return productRepository.save(product);
                });
    }

    @Override
    public Mono<Product> inactivate(String id) {
        log.info("Inactivando producto con ID: {}", id);
        return productRepository.findById(Long.valueOf(id))
                .flatMap(product -> {
                    product.setState("Inactive");
                    return productRepository.save(product);
                });
    }

}
