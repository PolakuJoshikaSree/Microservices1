package com.quizservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "quiz")
@Data
public class Quiz {

    @Id
    private String id;          
    private String title;
    private List<String> questionIds;   
}
