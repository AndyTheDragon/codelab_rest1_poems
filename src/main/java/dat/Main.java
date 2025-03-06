package dat;

import dat.config.HibernateConfig;
import dat.controllers.PoemController;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main
{
    final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    final static PoemController poemController = new PoemController(emf);

    public static void main(String[] args)
    {
        Javalin.create(config->{
            config.router.contextPath = "/api";
            config.router.apiBuilder(()->
            {
                path("poem", ()->{
                    get("/", poemController::getAll);
                    get("/{id}", poemController::getById);
                    post("/", poemController::create);
                    put("/{id}", poemController::update);
                    delete("/{id}", poemController::delete);
                });
            });

        }).start(7077);

    }
}