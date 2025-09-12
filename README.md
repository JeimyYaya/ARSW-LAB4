### Escuela Colombiana de Ingeniería
- Jeimy Yaya
### Arquitecturas de Software


#### API REST para la gestión de planos.

En este ejercicio se va a construír el componente BlueprintsRESTAPI, el cual permita gestionar los planos arquitectónicos de una prestigiosa compañia de diseño. La idea de este API es ofrecer un medio estandarizado e 'independiente de la plataforma' para que las herramientas que se desarrollen a futuro para la compañía puedan gestionar los planos de forma centralizada.
El siguiente, es el diagrama de componentes que corresponde a las decisiones arquitectónicas planteadas al inicio del proyecto:

![](img/CompDiag.png)

Donde se definió que:

* El componente BlueprintsRESTAPI debe resolver los servicios de su interfaz a través de un componente de servicios, el cual -a su vez- estará asociado con un componente que provea el esquema de persistencia. Es decir, se quiere un bajo acoplamiento entre el API, la implementación de los servicios, y el esquema de persistencia usado por los mismos.

Del anterior diagrama de componentes (de alto nivel), se desprendió el siguiente diseño detallado, cuando se decidió que el API estará implementado usando el esquema de inyección de dependencias de Spring (el cual requiere aplicar el principio de Inversión de Dependencias), la extensión SpringMVC para definir los servicios REST, y SpringBoot para la configurar la aplicación:


![](img/ClassDiagramC.png)

### Parte I

1. Integre al proyecto base suministrado los Beans desarrollados en el ejercicio anterior. Sólo copie las clases, NO los archivos de configuración. Rectifique que se tenga correctamente configurado el esquema de inyección de dependencias con las anotaciones @Service y @Autowired.

2. Modifique el bean de persistecia 'InMemoryBlueprintPersistence' para que por defecto se inicialice con al menos otros tres planos, y con dos asociados a un mismo autor.
```java
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
```

4. Configure su aplicación para que ofrezca el recurso "/blueprints", de manera que cuando se le haga una petición GET, retorne -en formato jSON- el conjunto de todos los planos. Para esto:

	* Modifique la clase BlueprintAPIController.
	* Haga que en esta misma clase se inyecte el bean de tipo BlueprintServices (al cual, a su vez, se le inyectarán sus dependencias de persisntecia y de filtrado de puntos).
```java
@RestController
@RequestMapping("/blueprints")
public class BlueprintAPIController {
    
    
    private static final Logger logger = Logger.getLogger(BlueprintAPIController.class.getName());

    @Autowired
    BlueprintsServices blueprintServices;

    @GetMapping
    public ResponseEntity<?> getAllBlueprints() {
        try {
            Set<Blueprint> all = blueprintServices.getAllBlueprints();
            return new ResponseEntity<>(all, HttpStatus.OK);
        } catch (BlueprintPersistenceException ex) {
            logger.log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

```

5. Verifique el funcionamiento de a aplicación lanzando la aplicación con maven:

	```bash
	$ mvn compile
	$ mvn spring-boot:run
	
	```
	Y luego enviando una petición GET a: http://localhost:8080/blueprints. Rectifique que, como respuesta, se obtenga un objeto jSON con una lista que contenga el detalle de los planos suministados por defecto, y que se haya aplicado el filtrado de puntos correspondiente.

#### Filtro de submuestreo
```bash
[{
	"author":"maria",
	"points":[
		{"x":0,"y":0}
	],
	"name":"casa"
},
{
	"author":"felipe",
	"points":[
		{"x":5,"y":5}
	],
	"name":"apartamento"
},
{
	"author":"alison",
	"points":[
		{"x":20,"y":20}
	],
	"name":"parque"
},
{
	"author":"jeimy",
	"points":[
		{"x":0,"y":0}
	],
	"name":"casa"
},
{
	"author":"jeimy",
	"points":[
		{"x":5,"y":5}
	],
	"name":"apartamento"
},
{
	"author":"maria",
	"points":[
		{"x":20,"y":20}
	],
	"name":"parque"
}]
```
<img width="905" height="220" alt="image" src="https://github.com/user-attachments/assets/97a79810-719d-4419-b377-1f59ab7e00e3" />   

#### Filtro de redundancia
```bash
[{
	"author":"maria",
	"points":[
		{"x":0,"y":0},
		{"x":10,"y":10}
	],
	"name":"casa"
},
{
	"author":"felipe",
	"points":[
		{"x":5,"y":5},
		{"x":15,"y":15}
	],
	"name":"apartamento"
},
{
	"author":"alison",
	"points":[
		{"x":20,"y":20},
		{"x":30,"y":30}
	],
	"name":"parque"
},
{
	"author":"jeimy",
	"points":[
		{"x":0,"y":0},
		{"x":10,"y":10}
	],
	"name":"casa"
},
{
	"author":"jeimy",
	"points":[
		{"x":5,"y":5},
		{"x":15,"y":15}
	],
	"name":"apartamento"
},
{
	"author":"maria",
	"points":[
		{"x":20,"y":20},
		{"x":30,"y":30}
	],
	"name":"parque"
}]
```
<img width="919" height="260" alt="image" src="https://github.com/user-attachments/assets/0fe51ecc-bba9-4045-8e66-6e9e7b5de569" />

7. Modifique el controlador para que ahora, acepte peticiones GET al recurso /blueprints/{author}, el cual retorne usando una representación jSON todos los planos realizados por el autor cuyo nombre sea {author}. Si no existe dicho autor, se debe responder con el código de error HTTP 404. Para esto, revise en [la documentación de Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html), sección 22.3.2, el uso de @PathVariable. De nuevo, verifique que al hacer una petición GET -por ejemplo- a recurso http://localhost:8080/blueprints/juan, se obtenga en formato jSON el conjunto de planos asociados al autor 'juan' (ajuste esto a los nombres de autor usados en el punto 2).

```java
    @GetMapping("/{author}")
    public ResponseEntity<?> getBlueprintsByAuthor(@PathVariable("author") String author) {
        try {
            Set<Blueprint> bps = blueprintServices.getBlueprintsByAuthor(author);
            return new ResponseEntity<>(bps, HttpStatus.OK);
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException ex) {
            logger.log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
```
#### JSON
```bash
[{
	"author":"jeimy",
	"points":[
		{"x":0,"y":0},
		{"x":10,"y":10}
	],
	"name":"casa"
},
{
	"author":"jeimy",
	"points":[
		{"x":5,"y":5},
		{"x":15,"y":15}
	],
	"name":"apartamento"
}]
```

<img width="761" height="175" alt="image" src="https://github.com/user-attachments/assets/70aedccd-094c-4e98-8b4e-18f009aa8ca4" />

#### Si no existe el autor:

<img width="768" height="340" alt="image" src="https://github.com/user-attachments/assets/d974c684-c3bf-40e6-8ecd-edf5b24eff9e" />


9. Modifique el controlador para que ahora, acepte peticiones GET al recurso /blueprints/{author}/{bpname}, el cual retorne usando una representación jSON sólo UN plano, en este caso el realizado por {author} y cuyo nombre sea {bpname}. De nuevo, si no existe dicho autor, se debe responder con el código de error HTTP 404. 

```java 
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<?> getBlueprintByAuthorAndName(@PathVariable("author") String author,
                                                        @PathVariable("bpname") String bpname) {
        try {
            Blueprint bp = blueprintServices.getBlueprint(author, bpname);
            return new ResponseEntity<>(bp, HttpStatus.OK);
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException ex) {
            logger.log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
```
#### JSON
```bash
{
	"author":"maria",
	"points":[
		{"x":0,"y":0},
		{"x":10,"y":10}
	],
	"name":"casa"}
```
<img width="665" height="176" alt="image" src="https://github.com/user-attachments/assets/aea9d096-270c-4e3f-810d-3c28034ea905" /> 

#### Si no existe el autor:
<img width="767" height="312" alt="image" src="https://github.com/user-attachments/assets/0fec549a-3cdd-483d-8bf8-11a6b1a2bfde" />


### Parte II

1.  Agregue el manejo de peticiones POST (creación de nuevos planos), de manera que un cliente http pueda registrar una nueva orden haciendo una petición POST al recurso ‘planos’, y enviando como contenido de la petición todo el detalle de dicho recurso a través de un documento jSON.

```	java
@PostMapping
    public ResponseEntity<?> addNewBlueprint(@RequestBody Blueprint bp) {
        try {
            blueprintServices.addNewBlueprint(bp);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException ex) {
            logger.log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error registrando el plano", HttpStatus.FORBIDDEN);
        }
    }
```	


2.  Para probar que el recurso ‘planos’ acepta e interpreta
    correctamente las peticiones POST, use el comando curl de Unix. 

	```	
	$ curl -i -X POST -HContent-Type:application/json -HAccept:application/json http://URL_del_recurso_ordenes -d '{ObjetoJSON}'
	```	
	Con lo anterior, registre un nuevo plano.
	
<img width="979" height="169" alt="image" src="https://github.com/user-attachments/assets/90870acb-6997-4b7f-bc11-1349c09b40b0" />

3. Teniendo en cuenta el autor y numbre del plano registrado, verifique que el mismo se pueda obtener mediante una petición GET al recurso '/blueprints/{author}/{bpname}' correspondiente.
```bash
{
	"author":"jeimy",
	"name":"gimnasio",
	"points":[
		{"x":0,"y":0},
		{"x":5,"y":5},
		{"x":10,"y":10}
	]
}
```		
 <img width="870" height="163" alt="image" src="https://github.com/user-attachments/assets/810d8d17-9965-476e-9559-d78ee048ad26" />


4. Agregue soporte al verbo PUT para los recursos de la forma '/blueprints/{author}/{bpname}', de manera que sea posible actualizar un plano determinado.

#### En el controlador:
```	java
@PutMapping("/{author}/{bpname}")
    public ResponseEntity<?> updateBlueprint(@PathVariable("author") String author,
                                            @PathVariable("bpname") String bpname,
                                            @RequestBody Blueprint updatedBp) {
        try {
            blueprintServices.updateBlueprint(author, bpname, updatedBp);
            return new ResponseEntity<>(HttpStatus.ACCEPTED); // 202 Accepted
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING, null, ex);
            return new ResponseEntity<>("Blueprint not found", HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException ex) {
            logger.log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error actualizando el plano", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
```
#### En BlueprintServices:
```	java
public void updateBlueprint(String author, String name, Blueprint updatedBp) throws BlueprintNotFoundException, BlueprintPersistenceException {
        bpp.updateBlueprint(author, name, updatedBp);
    }
```
#### InMemoryBlueprintPersistence:
```	java
@Override
    public void updateBlueprint(String author, String name, Blueprint updatedBp) throws BlueprintNotFoundException, BlueprintPersistenceException {
        Blueprint existing = blueprints.get(new Tuple<>(author, name));
        if (existing == null) {
            throw new BlueprintNotFoundException("No se encontró el plano para actualizar");
        }
        existing.setPoints(updatedBp.getPoints());
    }
```
#### Probamos el PUT:
*Antes*
<img width="749" height="162" alt="image" src="https://github.com/user-attachments/assets/1ab77f11-2764-4a96-bfe9-c50378542914" />


```bash
curl.exe -i -X PUT -H "Content-Type: application/json" -H "Accept: application/json" http://localhost:8080/blueprints/jeimy/apartamento -d "{\"author\":\"jeimy\",\"name\":\"apartamento\",\"points\":[{\"x\":99,\"y\":99},{\"x\":50,\"y\":50}]}"
```
*Después*

<img width="784" height="162" alt="image" src="https://github.com/user-attachments/assets/81ba58f7-0e6d-45d5-a3ac-4e9be366bab6" />


### Parte III

El componente BlueprintsRESTAPI funcionará en un entorno concurrente. Es decir, atederá múltiples peticiones simultáneamente (con el stack de aplicaciones usado, dichas peticiones se atenderán por defecto a través múltiples de hilos). Dado lo anterior, debe hacer una revisión de su API (una vez funcione), e identificar:

* Qué condiciones de carrera se podrían presentar?
* Cuales son las respectivas regiones críticas?

Ajuste el código para suprimir las condiciones de carrera. Tengan en cuenta que simplemente sincronizar el acceso a las operaciones de persistencia/consulta DEGRADARÁ SIGNIFICATIVAMENTE el desempeño de API, por lo cual se deben buscar estrategias alternativas.

Escriba su análisis y la solución aplicada en el archivo ANALISIS_CONCURRENCIA.txt  

### *Implementación*
- Ahora se utilizara una colección concurrente:
```java
private final ConcurrentHashMap<Tuple<String,String>,Blueprint> blueprints=new ConcurrentHashMap<>();
```
- Agregar nuevos planos con operación atómica:

```java
@Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprints.putIfAbsent(new Tuple<>(bp.getAuthor(),bp.getName()), bp) != null){
            throw new BlueprintPersistenceException("El blueprint ya existe: "+bp);
        }
        else{
            blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        }        
    }
```
- Actualizar planos:
```java
@Override
    public void updateBlueprint(String author, String name, Blueprint updatedBp) throws BlueprintNotFoundException, BlueprintPersistenceException {
        Tuple<String, String> key = new Tuple<>(author, name);
        Blueprint result = blueprints.computeIfPresent(key, (k, existing) -> {
            existing.setPoints(updatedBp.getPoints());
            return existing; 
        });

        if (result == null) {
            throw new BlueprintNotFoundException("No se encontró el plano para actualizar");
        }
    }
```
