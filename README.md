## Escuela Colombiana de Ingeniería

## Arquitecturas de Software
- Jeimy Yaya
# Componentes y conectores - Parte I.

#### Middleware- gestión de planos.
En este ejercicio se va a construír un modelo de clases para la capa lógica de una aplicación que permita gestionar planos arquitectónicos de una prestigiosa compañia de diseño. 

![](img/ClassDiagram1.png)

1. Configure la aplicación para que funcione bajo un esquema de inyección de dependencias, tal como se muestra en el diagrama anterior.


	Lo anterior requiere:

	* Agregar las dependencias de Spring.
	* Agregar la configuración de Spring.
	* Configurar la aplicación -mediante anotaciones- para que el esquema de persistencia sea inyectado al momento de ser creado el bean 'BlueprintServices'.
- *Nos aseguramos de que se encuntren las dependencias en el pom.*
```
<dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.2.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.2.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.3.30</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
```
- *Creamos una clase de configuración en spring*
``` java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"edu.eci.arsw.blueprints"})
public class AppConfig {
}
```
- *Ajustamos la clase BlueprintsServices*
```java
@Service
public class BlueprintsServices {

    private final BlueprintsPersistence bpp;

    @Autowired
    public BlueprintsServices(BlueprintsPersistence bpp) {
        this.bpp = bpp;
    }
```
- *Se usa @Repository en InMemoryBlueprintPersistence porque es la clase encargada de la persistencia. Esta anotación permite que Spring la detecte automáticamente como un bean de la capa de datos y pueda ser inyectada en los servicios, manteniendo la separación de responsabilidades.*
```java
@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{
```

2. Complete los operaciones getBluePrint() y getBlueprintsByAuthor(). Implemente todo lo requerido de las capas inferiores (por ahora, el esquema de persistencia disponible 'InMemoryBlueprintPersistence') agregando las pruebas correspondientes en 'InMemoryPersistenceTest'.

- *Extendemos la interfaz de BlueprintPersistence*
```java
 /**
     * 
     * @return all the blueprints
     */
    public Set<Blueprint> getAllBlueprints();

    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;
```
- *Complementamos los metodos de la clase BlueprintsServices:*
```java
    public void addNewBlueprint(Blueprint bp) {
        try {
            bpp.saveBlueprint(bp);
        } catch (edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException e) {
            throw new RuntimeException("Error saving blueprint", e);
        }
    }
    
    public Set<Blueprint> getAllBlueprints(){
        return new java.util.HashSet<>(bpp.getAllBlueprints());
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        return bpp.getBlueprint(author, name);
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        return bpp.getBlueprintsByAuthor(author); 
    }
    
}
```

- *En la clase InMemoryBlueprintPersistence implementamos los metodos __getAllBllueprints()__ y __getBlueprintsByAuthor()__:*
```java

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
    
```
- *Finalmente se crean las pruebas para los nuevos metodos:*
```java
@Test
    public void getBlueprintsByAuthorTest() throws Exception {
        InMemoryBlueprintPersistence ibpp = new InMemoryBlueprintPersistence();

        Point[] pts1 = new Point[]{new Point(5, 5), new Point(10, 10)};
        Blueprint bp1 = new Blueprint("jeimy", "casa", pts1);
        ibpp.saveBlueprint(bp1);

        Point[] pts2 = new Point[]{new Point(15, 15), new Point(20, 20)};
        Blueprint bp2 = new Blueprint("jeimy", "apartamento", pts2);
        ibpp.saveBlueprint(bp2);

        assertEquals("Author should have 2 blueprints",
                2, ibpp.getBlueprintsByAuthor("jeimy").size());
    }

    @Test(expected = BlueprintNotFoundException.class)
    public void getBlueprintsByNonExistingAuthorTest() throws Exception {
        InMemoryBlueprintPersistence ibpp = new InMemoryBlueprintPersistence();
        ibpp.getBlueprintsByAuthor("noexiste");
    }

    @Test
    public void getAllBlueprintsTest() throws Exception {
        InMemoryBlueprintPersistence ibpp = new InMemoryBlueprintPersistence();

        Point[] pts1 = new Point[]{new Point(1, 1), new Point(2, 2)};
        Blueprint bp1 = new Blueprint("anna", "plano1", pts1);
        ibpp.saveBlueprint(bp1);

        Point[] pts2 = new Point[]{new Point(3, 3), new Point(4, 4)};
        Blueprint bp2 = new Blueprint("mark", "plano2", pts2);
        ibpp.saveBlueprint(bp2);

        assertTrue("All blueprints should contain the saved ones",
                ibpp.getAllBlueprints().contains(bp1) && ibpp.getAllBlueprints().contains(bp2));
    }
  
}
```

3. Haga un programa en el que cree (mediante Spring) una instancia de BlueprintServices, y rectifique la funcionalidad del mismo: registrar planos, consultar planos, registrar planos específicos, etc.

- *Creamos un main para rectificar la funcionalidad:*
```java
public class MainApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        BlueprintsServices service = ctx.getBean(BlueprintsServices.class);

        Blueprint bp1 = new Blueprint("jeimy", "casa", new Point[]{new Point(0, 0), new Point(10, 10)});
        Blueprint bp2 = new Blueprint("jeimy", "apartamento", new Point[]{new Point(5, 5), new Point(15, 15)});
        Blueprint bp3 = new Blueprint("maria", "parque", new Point[]{new Point(20, 20), new Point(30, 30)});

        service.addNewBlueprint(bp1);
        service.addNewBlueprint(bp2);
        service.addNewBlueprint(bp3);

        System.out.println("Plano de jeimy - casa:");
        System.out.println(service.getBlueprint("jeimy", "casa"));

        System.out.println("\nPlanos de jeimy:");
        service.getBlueprintsByAuthor("jeimy").forEach(System.out::println);

        System.out.println("\nTodos los planos:");
        service.getAllBlueprints().forEach(System.out::println);
    }
}
```
*Y obtuvimos de salida:*   
<img width="409" height="246" alt="image" src="https://github.com/user-attachments/assets/0680064e-c190-448e-bab1-f33d0885a89f" />

4. Se quiere que las operaciones de consulta de planos realicen un proceso de filtrado, antes de retornar los planos consultados. Dichos filtros lo que buscan es reducir el tamaño de los planos, removiendo datos redundantes o simplemente submuestrando, antes de retornarlos. Ajuste la aplicación (agregando las abstracciones e implementaciones que considere) para que a la clase BlueprintServices se le inyecte uno de dos posibles 'filtros' (o eventuales futuros filtros). No se contempla el uso de más de uno a la vez:
	* (A) Filtrado de redundancias: suprime del plano los puntos consecutivos que sean repetidos.
	* (B) Filtrado de submuestreo: suprime 1 de cada 2 puntos del plano, de manera intercalada.
- *Primero creamos una interfaz el comportamineto generico de los filtros:*

```java
public interface BlueprintFilter {
    Blueprint applyFilter(Blueprint bp);

}
```
- *Implementamos el filtro de redundancias:*

```java
@Component("redundancyFilter") 
public class RedundancyFilter implements BlueprintFilter {

    @Override
    public Blueprint applyFilter(Blueprint bp) {
        List<Point> filtered = new ArrayList<>();
        Point prev = null;
        for (Point p : bp.getPoints()) {
            if (prev == null || !(prev.getX() == p.getX() && prev.getY() == p.getY())) {
                filtered.add(p);
            }
            prev = p;
        }
        return new Blueprint(bp.getAuthor(), bp.getName(), filtered.toArray(new Point[0]));
    }
}
```
- *Implementamos el filtro de submuestreo:*
```java
@Component("subsamplingFilter")
public class SubsamplingFilter implements BlueprintFilter {

    @Override
    public Blueprint applyFilter(Blueprint bp) {
        List<Point> filtered = new ArrayList<>();
        List<Point> points = bp.getPoints();

        for (int i = 0; i < points.size(); i++) {
            if (i % 2 == 0) { 
                filtered.add(points.get(i));
            }
        }
        return new Blueprint(bp.getAuthor(), bp.getName(), filtered.toArray(new Point[0]));
    }
```

- *Ahora se modifica la clase BlueprintsServices para que use el filtro:*
```java

@Service
public class BlueprintsServices {

    private final BlueprintsPersistence bpp;

    private final BlueprintFilter filter;

    @Autowired
    public BlueprintsServices(BlueprintsPersistence bpp, BlueprintFilter filter) {
        this.bpp = bpp;
        this.filter = filter;
    }
    
    public Set<Blueprint> getAllBlueprints(){
        return bpp.getAllBlueprints().stream()
                .map(filter::applyFilter)
                .collect(Collectors.toSet());
    }

    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        return filter.applyFilter(bpp.getBlueprint(author, name));
    }
    
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        return bpp.getBlueprintsByAuthor(author).stream()
                .map(filter::applyFilter)
                .collect(Collectors.toSet());
    }
    
}

```
- *Finalmente para decidir que filtro usar realizamos lo siguiente en la clase BlueprintsServices:*

```java
@Qualifier("redundancyFilter") // o "subsamplingFilter"
private final BlueprintFilter filter;
```

5. Agrege las pruebas correspondientes a cada uno de estos filtros, y pruebe su funcionamiento en el programa de prueba, comprobando que sólo cambiando la posición de las anotaciones -sin cambiar nada más-, el programa retorne los planos filtrados de la manera (A) o de la manera (B). 
- *Pruebas:*
```java
    @Test
    public void testRedundancyFilterRemovesConsecutiveDuplicates() {
        RedundancyFilter filter = new RedundancyFilter();

        Point[] points = {
                new Point(0, 0),
                new Point(0, 0), // duplicado
                new Point(1, 1),
                new Point(1, 1), // duplicado
                new Point(2, 2)
        };

        Blueprint bp = new Blueprint("jeimy", "casa", points);
        Blueprint filtered = filter.applyFilter(bp);

        assertEquals("Debe remover duplicados consecutivos", 3, filtered.getPoints().size());

        assertEquals(0, filtered.getPoints().get(0).getX());
        assertEquals(0, filtered.getPoints().get(0).getY());

        assertEquals(1, filtered.getPoints().get(1).getX());
        assertEquals(1, filtered.getPoints().get(1).getY());

        assertEquals(2, filtered.getPoints().get(2).getX());
        assertEquals(2, filtered.getPoints().get(2).getY());
    }

    @Test
    public void testSubsamplingFilterRemovesEveryOtherPoint() {
        SubsamplingFilter filter = new SubsamplingFilter();

        Point[] points = {
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3),
                new Point(4, 4)
        };

        Blueprint bp = new Blueprint("jeimy", "apartamento", points);
        Blueprint filtered = filter.applyFilter(bp);

        assertEquals("Debe quedarse con 1 de cada 2 puntos", 3, filtered.getPoints().size());

        assertEquals(0, filtered.getPoints().get(0).getX());
        assertEquals(0, filtered.getPoints().get(0).getY());

        assertEquals(2, filtered.getPoints().get(1).getX());
        assertEquals(2, filtered.getPoints().get(1).getY());

        assertEquals(4, filtered.getPoints().get(2).getX());
        assertEquals(4, filtered.getPoints().get(2).getY());
    }
}
```
- *Plano filtrado de por redundancia (A):*
```java
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
```   
<img width="536" height="50" alt="image" src="https://github.com/user-attachments/assets/d4090ad4-3aa9-49b3-8c9b-6321c8875c2c" />   

- *Plano filtrado de por submuestreo (B):*
   
```java
Blueprint subsample = new Blueprint("maria", "submuestreo",
                new Point[]{
                        new Point(0, 0),
                        new Point(1, 1),
                        new Point(2, 2),
                        new Point(3, 3),
                        new Point(4, 4)
                });

        service.addNewBlueprint(subsample);
        System.out.println("\nOriginal (submuestreo): (0,0),(1,1),(2,2),(3,3),(4,4)");
        System.out.println("Filtrado: " + puntosToString(service.getBlueprint("maria", "submuestreo")));
    
```
   
<img width="533" height="50" alt="image" src="https://github.com/user-attachments/assets/5b1b7204-913f-4faf-8d7f-962efbe348e2" />




