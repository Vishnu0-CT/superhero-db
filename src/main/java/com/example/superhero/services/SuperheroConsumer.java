package com.example.superhero.services;

import com.example.superhero.repositories.SuperheroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Service
public class SuperheroConsumer {
    private SuperheroService superheroService;

    private SuperheroRepository superheroRepository;


//    var superhero = superheroRepository.findAllByName(name);
//        if (superhero.size() == 0) {
//        throw new RuntimeException("No superheroes with the name: " + name);
//    }
//        else {
//        superhero.get(0).setName(updatedSuperhero.getName());
//        superhero.get(0).setPower(updatedSuperhero.getPower());
//        superhero.get(0).setUniverse(updatedSuperhero.getUniverse());
//        return superheroRepository.save(superhero.get(0));
//    }

}
