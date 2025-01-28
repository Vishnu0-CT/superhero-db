package com.example.superhero.repositories;

import com.example.superhero.dto.Superhero;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SuperheroRepository extends MongoRepository<Superhero, String> {
    List<Superhero> findAllByUniverse(String universe);
    List<Superhero> findAllByName(String name);
    List<Superhero> removeByName(String name);
}
