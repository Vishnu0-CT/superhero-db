package com.example.superhero.services;

import com.example.superhero.dto.Superhero;
import com.example.superhero.repositories.SuperheroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

@Service
public class SuperheroConsumer implements CommandLineRunner {
    private SuperheroRepository superheroRepository;

    private SqsService sqsService;

    public SuperheroConsumer(SqsService sqsService,
                             SuperheroRepository superheroRepository) {
        this.sqsService = sqsService;
        this.superheroRepository = superheroRepository;
    }

    @Value("${app.sqs.queueUrl}")
    private String queueUrl;

    private void consumeAndUpdate() {
        Map<String, Object> returnRaw = sqsService.receiveAndDeleteMessage(queueUrl);

        Superhero superheroToEdit = superheroRepository.findAllByName(returnRaw.get("name").toString()).get(0);

        superheroToEdit.setName(returnRaw.get("name").toString());
        superheroToEdit.setPower(returnRaw.get("power").toString());
        superheroToEdit.setUniverse(returnRaw.get("universe").toString());
        superheroRepository.save(superheroToEdit);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        while (true) {
            Map<String, Object> messagePayload = sqsService.receiveAndDeleteMessage(queueUrl);

            if (messagePayload != null) {
                updateSuperheroInDb(messagePayload);
            } else {
                Thread.sleep(1000);
            }
        }
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
