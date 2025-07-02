package com.projetfilrougeapi.apifilrouge.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service de validation des noms pour détecter les insultes
 */
@Service
public class NameValidator {
    private static List<String> insultList;

    static {
        try {
            // Charger la liste d'insultes depuis le fichier JSON
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("/json/insulte.json");

            // Lire le JSON comme un JsonNode pour accéder à la propriété "insultes"
            JsonNode rootNode = mapper.readTree(resource.getInputStream());
            JsonNode insultesNode = rootNode.get("insultes");

            // Convertir le tableau JSON en liste de chaînes
            insultList = mapper.convertValue(insultesNode, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            insultList = new ArrayList<>();
            System.err.println("Erreur lors du chargement du fichier d'insultes: " + e.getMessage());
        }
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false; // Le nom ne peut pas être null ou vide
        }

        for (String insult : insultList) {
            String lowerCaseInsult = insult.toLowerCase();
            // Vérifier si le nom contient l'insulte
            if (name.toLowerCase().contains(lowerCaseInsult)) {
                return false; // Le nom contient une insulte
            }
        }

        return true;
    }
}