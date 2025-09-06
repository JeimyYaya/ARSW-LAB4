/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filters.BlueprintFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class BlueprintsServices {

    private final BlueprintsPersistence bpp;

    @Qualifier("subsamplingFilter")
    private final BlueprintFilter filter;

    @Autowired
    public BlueprintsServices(BlueprintsPersistence bpp, @Qualifier("subsamplingFilter") BlueprintFilter filter) {
        this.bpp = bpp;
        this.filter = filter;
    }

    public void addNewBlueprint(Blueprint bp) {
        try {
            bpp.saveBlueprint(bp);
        } catch (edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException e) {
            throw new RuntimeException("Error saving blueprint", e);
        }
    }
    
    public Set<Blueprint> getAllBlueprints(){
        return bpp.getAllBlueprints().stream()
                .map(filter::applyFilter)
                .collect(Collectors.toSet());
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        return filter.applyFilter(bpp.getBlueprint(author, name));
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        return bpp.getBlueprintsByAuthor(author).stream()
                .map(filter::applyFilter)
                .collect(Collectors.toSet());
    }
    
}
