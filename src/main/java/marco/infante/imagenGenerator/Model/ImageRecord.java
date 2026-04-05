package marco.infante.imagenGenerator.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_images")
public class ImageRecord {

    @Id
    private String id;

    private String prompt;
    private String imageUrl;      
    private String imageUrl2;       
    private Integer styleId;
    private String size;
    private String status;      
    private String errorMessage;
    private LocalDateTime createdAt;

    // Constructor sin id
    public ImageRecord(String prompt, String imageUrl, Integer styleId,
                       String size, String status, String errorMessage,
                       LocalDateTime createdAt) {
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.styleId = styleId;
        this.size = size;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }
}