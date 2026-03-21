package pe.edu.vallegrande.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Data
@Table(name = "product")
public class Product {

    @Id
    @Column(value = "id")
    private Long id;

    @Column(value = "name")
    private String name;

    @Column(value = "description")
    private String description;

    @Column(value = "price")
    private Double price;

    @Column(value = "category")
    private String category;      

    @Column(value = "stock")
    private Integer stock;

    @Column(value = "code")
    private Long code;            

    @Column(value = "created_at")
    private LocalDate createdAt;  

    @Column(value = "state")
    private String state;

}