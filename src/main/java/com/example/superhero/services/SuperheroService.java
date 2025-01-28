package com.example.superhero.services;

import com.example.superhero.dto.Superhero;
import com.example.superhero.dto.SuperheroRequestBody;
import com.example.superhero.repositories.SuperheroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuperheroService {
    private final SuperheroRepository superheroRepository;

    @Autowired
    public SuperheroService(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    public Object getSuperheroService(String name, String universe) throws Exception {
        if (!name.equals("")) {
            System.out.println("Name: " + name);
            return getByName(name);
        } else if (!universe.equals("")) {
            System.out.println("Universe: " + universe);
            return getByUniverse(universe);
        } else {
            throw new RuntimeException("Name or Universe must be provided");
        }
    }

    private Object getByName(String name) {
        var ans = superheroRepository.findAllByName(name);
        if (ans.size() == 0) {
            throw new RuntimeException("No superheroes with the name: " + name);
        }
        else {
            return ans;
        }
    }

    private Object getByUniverse(String universe) {
        var ans = superheroRepository.findAllByUniverse(universe);
        if (ans.size() == 0) {
            throw new RuntimeException("No superheroes found for universe: " + universe);
        }
        else {
            return ans;
        }
    }

    public Superhero persistSuperhero(SuperheroRequestBody requestBody) {
        Superhero superhero = new Superhero();
        superhero.setName(requestBody.getName());
        superhero.setPower(requestBody.getPower());
        superhero.setUniverse(requestBody.getUniverse());

        return superheroRepository.save(superhero);
    }

    public Superhero editSuperhero (String name, SuperheroRequestBody updatedSuperhero) {
        var superhero = superheroRepository.findAllByName(name);
        if (superhero.size() == 0) {
            throw new RuntimeException("No superheroes with the name: " + name);
        }
        else {
            superhero.get(0).setName(updatedSuperhero.getName());
            superhero.get(0).setPower(updatedSuperhero.getPower());
            superhero.get(0).setUniverse(updatedSuperhero.getUniverse());
            return superheroRepository.save(superhero.get(0));
        }
    }
    public Object removeSuperhero(String name) {
        var ans = superheroRepository.removeByName(name);
        if (ans.size() == 0) {
            throw new RuntimeException("No superheroes with the name: " + name);
        }
        else {
            return "Superhero with name: " + name + " removed";
        }
    }
}