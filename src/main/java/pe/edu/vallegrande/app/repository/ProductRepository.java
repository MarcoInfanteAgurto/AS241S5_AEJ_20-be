package pe.edu.vallegrande.app.repository;

import pe.edu.vallegrande.app.model.Product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

}
