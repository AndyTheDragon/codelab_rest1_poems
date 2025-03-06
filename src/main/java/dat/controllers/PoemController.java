package dat.controllers;

import dat.dao.GenericDao;
import dat.dto.ErrorMessage;
import dat.dto.PoemDTO;
import dat.entities.Poem;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;


public class PoemController
{
    private final GenericDao genericDao;


    public PoemController(EntityManagerFactory emf)
    {
        genericDao = GenericDao.getInstance(emf);
    }

    public void getAll(Context ctx)
    {
        try
        {
            ctx.json(genericDao.findAll(Poem.class));
        }
        catch (Exception ex)
        {
            ErrorMessage error = new ErrorMessage("Error getting poems");
            ctx.status(404).json(error);
        }
    }

    public void getById(Context ctx)
    {

        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            PoemDTO foundPoem = new PoemDTO(genericDao.read(Poem.class, id));
            ctx.json(foundPoem);

        } catch (Exception ex){
            ErrorMessage error = new ErrorMessage("No poem with that id");
            ctx.status(404).json(error);
        }
    }

    public void create(Context ctx)
    {
        try
        {
            PoemDTO incomingPoem = ctx.bodyAsClass(PoemDTO.class);
            Poem poem = new Poem(incomingPoem);
            Poem createdPoem = genericDao.create(poem);
            ctx.json(new PoemDTO(createdPoem));
        }
        catch (Exception ex)
        {
            ErrorMessage error = new ErrorMessage("Error creating poem");
            ctx.status(400).json(error);
        }
    }

    public void update(Context ctx)
    {
        try
        {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PoemDTO incomingPoem = ctx.bodyAsClass(PoemDTO.class);
            Poem poem = new Poem(incomingPoem);
            poem.setId((long) id);
            Poem updatedPoem = genericDao.update(poem);
            PoemDTO returnedPoem = new PoemDTO(updatedPoem);
            ctx.json(returnedPoem);
        }
        catch (Exception ex)
        {
            ErrorMessage error = new ErrorMessage("Error updating poem");
            ctx.status(400).json(error);
        }
    }

    public void delete(Context ctx)
    {
        try
        {
            long id = Long.parseLong(ctx.pathParam("id"));
            genericDao.delete(Poem.class, id);
            ctx.status(204);
        }
        catch (Exception ex)
        {
            ErrorMessage error = new ErrorMessage("Error deleting poem");
            ctx.status(400).json(error);
        }
    }

}
