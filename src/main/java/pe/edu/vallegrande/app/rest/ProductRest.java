package pe.edu.vallegrande.app.rest;

import pe.edu.vallegrande.app.model.Product;
import pe.edu.vallegrande.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/product")
public class ProductRest {

    private final ProductService productService;

    @Autowired
    public ProductRest(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Flux<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Product> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/save")
    public Mono<Product> save(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/update")
    public Mono<Product> update(@RequestBody Product product) {
        return productService.update(product);
    }

    @PutMapping("/activate/{id}")
    public Mono<Product> activate(@PathVariable String id) {
        return productService.activate(id);
    }

    @PutMapping("/inactivate/{id}")
    public Mono<Product> inactivate(@PathVariable String id) {
        return productService.inactivate(id);
    }

}
