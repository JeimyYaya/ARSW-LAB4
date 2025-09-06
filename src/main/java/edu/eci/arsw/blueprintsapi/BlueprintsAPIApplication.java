package edu.eci.arsw.blueprintsapi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"edu.eci.arsw.blueprints"})
public class BlueprintsAPIApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(BlueprintsAPIApplication.class, args);
    }
/*@Bean
    public CommandLineRunner loadData(BlueprintsServices service) {
        return (args) -> {
            //Planos básicos
            Blueprint bp1 = new Blueprint("jeimy", "casa",
                    new Point[]{new Point(0, 0), new Point(10, 10)});
            Blueprint bp2 = new Blueprint("jeimy", "apartamento",
                    new Point[]{new Point(5, 5), new Point(15, 15)});
            Blueprint bp3 = new Blueprint("maria", "parque",
                    new Point[]{new Point(20, 20), new Point(30, 30)});

            service.addNewBlueprint(bp1);
            service.addNewBlueprint(bp2);
            service.addNewBlueprint(bp3);

            //Redundancia
            Blueprint redundant = new Blueprint("jeimy", "redundante",
                    new Point[]{
                            new Point(0, 0),
                            new Point(0, 0), // duplicado
                            new Point(1, 1),
                            new Point(1, 1), // duplicado
                            new Point(2, 2)
                    });
            service.addNewBlueprint(redundant);

            System.out.println("Original (redundante): (0,0),(0,0),(1,1),(1,1),(2,2)");
            System.out.println("Filtrado: " + puntosToString(service.getBlueprint("jeimy", "redundante")));


            //Submuestreo
            Blueprint subsample = new Blueprint("maria", "submuestreo",
                    new Point[]{
                            new Point(0, 0),
                            new Point(1, 1),
                            new Point(2, 2),
                            new Point(3, 3),
                            new Point(4, 4)
                    });
            service.addNewBlueprint(subsample);
            System.out.println("Original (submuestreo): (0,0),(1,1),(2,2),(3,3),(4,4)");
            System.out.println("Ejemplo consulta subsample (maria): " +
                    puntosToString(service.getBlueprint("maria", "submuestreo")));
        };
    }

    private static String puntosToString(Blueprint bp) {
        StringBuilder sb = new StringBuilder();
        for (Point p : bp.getPoints()) {
            sb.append("(").append(p.getX()).append(",").append(p.getY()).append("),");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }*/
}
