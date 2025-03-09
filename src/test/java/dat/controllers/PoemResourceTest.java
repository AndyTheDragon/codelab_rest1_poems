package dat.controllers;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.dto.PoemDTO;
import dat.entities.Poem;
import dat.routes.Routes;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PoemResourceTest
{
    private static EntityManagerFactory emf;
    ObjectMapper objectMapper = new ObjectMapper();
    Poem t1, t2;


    @BeforeAll
    static void setUpAll()
    {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        ApplicationConfig.getInstance()
                .initiateServer()
                .setRoute(Routes.getRoutes(emf))
                .handleException()
                .startServer(7078);
        RestAssured.baseURI = "http://localhost:7078/api";
    }

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            //TestEntity[] entities = EntityPopulator.populate(genericDAO);
            t1 = new Poem("TestEntityA");
            t2 = new Poem("TestEntityB");
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Poem ").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE poem_id_seq RESTART WITH 1");
            em.persist(t1);
            em.persist(t2);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    void getAll()
    {
        given().when().get("/poem").then().statusCode(200).body("size()", equalTo(2));
    }

    @Test
    void getById()
    {
        given().when().get("/poem/1").then().statusCode(200).body("id", equalTo(1));
    }

    @Test
    void create()
    {
        Poem poem = new Poem("New poem", "Lorenzo", "hi hi hi hi hi hi");
        try
        {
            String json = objectMapper.writeValueAsString(new PoemDTO(poem));
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .post("/poem")
                    .then()
                    .statusCode(200)
                    .body("title", equalTo("New poem")); // check here??
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void update()
    {
        Poem poem = new Poem("New poem2", "GPT", "hi hi hi hi hi hi");
        try
        {
            String json = objectMapper.writeValueAsString(new PoemDTO(poem));
            given().when()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(json)
                    .put("/poem/1") // double check id
                    .then()
                    .statusCode(200)
                    .body("title", equalTo("New poem2"))
                    .body("author", equalTo("GPT"))
                    .body("content", equalTo("hi hi hi hi hi hi"));
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void delete()
    {
    }
}