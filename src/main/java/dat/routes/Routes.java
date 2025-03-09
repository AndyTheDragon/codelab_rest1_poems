package dat.routes;

import dat.controllers.PoemController;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private static PoemController poemController;
    private static final Logger logger = LoggerFactory.getLogger(Routes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        poemController = new PoemController(emf);
        return () -> {
            path("/poem", () -> {
                get(poemController::getAll);
                post(poemController::create);
                get("/{id}", poemController::getById);
                put("/{id}", poemController::update);
                delete("/{id}", poemController::delete);
            });
        };
    }

}
