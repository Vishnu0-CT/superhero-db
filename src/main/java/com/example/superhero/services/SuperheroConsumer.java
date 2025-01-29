package com.example.superhero.services;

import com.example.superhero.dto.Superhero;
import com.example.superhero.repositories.SuperheroRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SuperheroConsumer {
    private final SuperheroRepository superheroRepository;
    private final ObjectMapper objectMapper;

    public SuperheroConsumer(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
        this.objectMapper = new ObjectMapper();
    }

    @SqsListener(value = "${app.sqs.queueUrl}")
    public void receiveMessage(String message) throws JsonProcessingException {
            System.out.println("Received message from SQS: " + message);
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            updateSuperheroInDb(payload);

    }

    private void updateSuperheroInDb(Map<String, Object> payload) {
        String name = payload.get("name").toString();
        String power = payload.get("power").toString();
        String universe = payload.get("universe").toString();

        List<Superhero> heroes = superheroRepository.findAllByName(name);
        if (heroes.isEmpty()) {
            throw new RuntimeException("No superhero found with name: " + name);
        }

        Superhero hero = heroes.get(0);
        hero.setName(name);
        hero.setPower(power);
        hero.setUniverse(universe);
        superheroRepository.save(hero);
        System.out.println("Updated superhero in mongo: " + hero);
    }
}