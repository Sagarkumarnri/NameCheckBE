package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NameMatchController {

    @Autowired
    private NameMatcherService nameMatcherService;

    @PostMapping("/match")
    public Map<String, Object> matchNames(@RequestBody Map<String, String> names) throws IOException {
        String correctName = names.get("correctName");
        String givenName = names.get("givenName");

        // Call service to compute similarity
        double similarityScore = nameMatcherService.matchNames(correctName, givenName);

        return Map.of("similarity", similarityScore +"%");
    }
}
