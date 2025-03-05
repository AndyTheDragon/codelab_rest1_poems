package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenericDaoTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final GenericDao genericDAO = GenericDao.getInstance(emf);
    private static Poem t1;
    private static Poem t2;

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
                em.createNativeQuery("ALTER SEQUENCE testentity_id_seq RESTART WITH 1");
                em.persist(t1);
                em.persist(t2);
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterAll
    void tearDown()
    {
        if (emf != null && emf.isOpen())
        {
            emf.close();
            System.out.println("EntityManagerFactory closed");
        }
    }

    @Test
    void getInstance()
    {
        assertNotNull(emf);
    }

    @Test
    void create()
    {
        // Arrange
        Poem t3 = new Poem("TestEntityC");

        // Act
        Poem result = genericDAO.create(t3);

        // Assert
        assertThat(result, samePropertyValuesAs(t3));
        assertNotNull(result);
        try (EntityManager em = emf.createEntityManager())
        {
            Poem found = em.find(Poem.class, result.getId());
            assertThat(found, samePropertyValuesAs(t3));
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM Poem t", Long.class).getSingleResult();
            assertThat(amountInDb, is(3L));
        }

    }

    @Test
    void createMany()
    {
        // Arrange
        Poem t3 = new Poem("TestEntityC");
        Poem t4 = new Poem("TestEntityD");
        List<Poem> testEntities = List.of(t3, t4);

        // Act
        List<Poem> result = genericDAO.create(testEntities);

        // Assert
        assertThat(result.get(0), samePropertyValuesAs(t3));
        assertThat(result.get(1), samePropertyValuesAs(t4));
        assertNotNull(result);
        try (EntityManager em = emf.createEntityManager())
        {
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM Poem t", Long.class).getSingleResult();
            assertThat(amountInDb, is(4L));
        }
    }

    @Test
    void read()
    {
        // Arrange
        Poem expected = t1;

        // Act
        Poem result = genericDAO.read(Poem.class, t1.getId());

        // Assert
        assertThat(result, samePropertyValuesAs(expected));
    }

    @Test
    void read_notFound()
    {
        // Act
        Poem result = genericDAO.read(Poem.class, 1000L);

        // Assert
        assertNull(result);
    }

    @Test
    void findAll()
    {
        // Arrange
        List<Poem> expected = List.of(t1, t2);

        // Act
        List<Poem> result = genericDAO.findAll(Poem.class);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(expected.get(0)));
        assertThat(result.get(1), samePropertyValuesAs(expected.get(1)));
    }

    @Test
    void update()
    {
        // Arrange
        t1.setTitle("UpdatedName");

        // Act
        Poem result = genericDAO.update(t1);

        // Assert
        assertThat(result, samePropertyValuesAs(t1));

    }

    @Test
    void updateMany()
    {
        // Arrange
        t1.setTitle("UpdatedName");
        t2.setTitle("UpdatedName");
        List<Poem> testEntities = List.of(t1, t2);

        // Act
        List<Poem> result = genericDAO.update(testEntities);

        // Assert
        assertNotNull(result);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), samePropertyValuesAs(t1));
        assertThat(result.get(1), samePropertyValuesAs(t2));
    }

    @Test
    void delete()
    {
        // Act
        genericDAO.delete(t1);

        // Assert
        try (EntityManager em = emf.createEntityManager())
        {
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM Poem t", Long.class).getSingleResult();
            assertThat(amountInDb, is(1L));
            Poem found = em.find(Poem.class, t1.getId());
            assertNull(found);
        }
    }

    @Test
    void delete_byId()
    {
        // Act
        genericDAO.delete(Poem.class, t2.getId());

        // Assert
        try (EntityManager em = emf.createEntityManager())
        {
            Long amountInDb = em.createQuery("SELECT COUNT(t) FROM Poem t", Long.class).getSingleResult();
            assertThat(amountInDb, is(1L));
            Poem found = em.find(Poem.class, t2.getId());
            assertNull(found);
        }
    }
}