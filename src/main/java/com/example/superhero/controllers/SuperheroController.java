package com.example.superhero.controllers;

import com.example.superhero.config.LocalSQSConfig;
import com.example.superhero.dto.Superhero;
import com.example.superhero.dto.SuperheroRequestBody;
import com.example.superhero.repositories.SuperheroRepository;
import com.example.superhero.services.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.example.superhero.services.SuperheroService;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@RestController
public class SuperheroController {
    private SuperheroService superheroService;

    @Autowired
    public SuperheroController(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }

    @Autowired
    private LocalSQSConfig sqsConfig;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private SqsService sqsService;

    @Autowired
    private SuperheroRepository superheroRepository;

    @Value("${app.sqs.queueUrl}")
    private String queueUrl;

    @GetMapping("/superhero")
    public String getSuperhero(@RequestParam(value = "name", defaultValue = "") String name,
                               @RequestParam(value = "universe", defaultValue = "") String universe)
            throws Exception {
        return superheroService.getSuperheroService(name, universe).toString();
    }

    @PostMapping("/superhero")
    public Superhero persistSuperhero(@RequestBody SuperheroRequestBody superhero){
        return superheroService.persistSuperhero(superhero);
    }

    @PutMapping("/superhero/{name}")
    public Superhero putSuperhero(@PathVariable String name, @RequestBody SuperheroRequestBody updatedSuperhero) {
        return superheroService.editSuperhero(name, updatedSuperhero);
    }

    @DeleteMapping("/superhero/{name}")
    public Object deleteSuperhero(@PathVariable String name) {
        return superheroService.removeSuperhero(name);
    }

    @PostMapping("/update_async")
    public String sendMessageToQueue(@RequestBody Map<String, Object> payload) {

        var ans = superheroRepository.findAllByName(payload.get("name").toString());
        if (ans.size() == 0) {
            throw new RuntimeException("No superheroes with that name");
        }
        // Log or process payload as needed
        System.out.println("Received payload: " + payload);

        // Push the payload to SQS
        sqsService.sendJsonMessage(queueUrl, payload);

        return "Message accepted and pushed to SQS.";
    }

    @GetMapping("/consume")
    public Object popFromQueue() {
        Map<String, Object> returnRaw = sqsService.receiveAndDeleteMessage(queueUrl);
        Superhero updateData = new Superhero();
        updateData.setName(returnRaw.get("name").toString());
        updateData.setPower(returnRaw.get("power").toString());
        updateData.setUniverse(returnRaw.get("universe").toString());
        superheroRepository.save(updateData);
        return "Superhero updated";
    }

}
