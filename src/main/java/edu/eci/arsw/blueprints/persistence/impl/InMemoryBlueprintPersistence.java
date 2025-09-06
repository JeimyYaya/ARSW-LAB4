/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;


import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

/**
 *
 * @author hcadavid
 */
@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    private final Map<Tuple<String,String>,Blueprint> blueprints=new HashMap<>();

    public InMemoryBlueprintPersistence() {
        //load stub data
        Point[] pts1 = new Point[]{ new Point(0,0), new Point(10,10) };
        Blueprint bp1 = new Blueprint("jeimy", "casa", pts1);
        blueprints.put(new Tuple<>(bp1.getAuthor(), bp1.getName()), bp1);

        Point[] pts2 = new Point[]{ new Point(5,5), new Point(15,15) };
        Blueprint bp2 = new Blueprint("jeimy", "apartamento", pts2);
        blueprints.put(new Tuple<>(bp2.getAuthor(), bp2.getName()), bp2);

        Point[] pts3 = new Point[]{ new Point(20,20), new Point(30,30) };
        Blueprint bp3 = new Blueprint("maria", "parque", pts3);
        blueprints.put(new Tuple<>(bp3.getAuthor(), bp3.getName()), bp3);

        Point[] pts4 = new Point[]{ new Point(0,0), new Point(10,10) };
        Blueprint bp4 = new Blueprint("maria", "casa", pts4);
        blueprints.put(new Tuple<>(bp4.getAuthor(), bp4.getName()), bp4);

        Point[] pts5 = new Point[]{ new Point(5,5), new Point(15,15) };
        Blueprint bp5 = new Blueprint("felipe", "apartamento", pts5);
        blueprints.put(new Tuple<>(bp5.getAuthor(), bp5.getName()), bp5);

        Point[] pts6 = new Point[]{ new Point(20,20), new Point(30,30) };
        Blueprint bp6 = new Blueprint("alison", "parque", pts6);
        blueprints.put(new Tuple<>(bp6.getAuthor(), bp6.getName()), bp6);
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprints.containsKey(new Tuple<>(bp.getAuthor(),bp.getName()))){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
        else{
            blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        }        
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        return blueprints.get(new Tuple<>(author, bprintname));
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(blueprints.values());
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> result = new HashSet<>();
        for (Map.Entry<Tuple<String, String>, Blueprint> entry : blueprints.entrySet()) {
            if (entry.getKey().getElem1().equals(author)) {
                result.add(entry.getValue());
            }
        }
        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }
        return result;
    }    
    
}
