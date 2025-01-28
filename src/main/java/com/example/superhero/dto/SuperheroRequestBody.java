package com.example.superhero.dto;

import lombok.Data;

@Data
public class SuperheroRequestBody {
    private String name;
    private String power;
    private String universe;
}