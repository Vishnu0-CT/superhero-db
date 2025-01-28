package com.example.superhero.dto;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "superheroes")
public class Superhero {
    @Id
    private String id;
    private String name;
    private String power;
    private String universe;

}
