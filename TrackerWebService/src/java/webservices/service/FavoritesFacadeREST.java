/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.service;

import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import webservices.Favorites;
import webservices.FavoritesPK;
import webservices.User;

/**
 *
 * @author Alessio Oglialoro, Daniele Saitta
 */
@Stateless
@Path("webservices.favorites")
public class FavoritesFacadeREST extends AbstractFacade<Favorites> {

    @PersistenceContext(unitName = "TrackerWebServicePU")
    private EntityManager em;

    public FavoritesFacadeREST() {
        super(Favorites.class);
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String createFavorites(Favorites entity) {
        try {
            Query q = getEntityManager().createQuery("SELECT f FROM Favorites f WHERE f.favoritesPK.routename = :routename AND f.favoritesPK.username = :username");
            q.setParameter("routename", entity.getFavoritesPK().getRoutename());
            q.setParameter("username", entity.getFavoritesPK().getUsername());
            Favorites favorite = (Favorites) q.getSingleResult();
            if (favorite != null) {
                return "no";
            }
        } catch (NoResultException ex) {
            System.out.println("Favorite non presente");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            super.create(entity);
            return "yes";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "no";
        }
    }
    
    @PUT
    @Path("{routename}/{username}")
    @Consumes({"application/json"})
    public void edit(@PathParam("routename") String routename, @PathParam("username") String username, Favorites entity) {
        super.edit(entity);
    }

    @GET
    @Path("delete/{routename}/{username}")
    @Produces({"application/json"})
    public String removeFavorites(@PathParam("routename") String routename, @PathParam("username") String username) {
        try {
            Query q = getEntityManager().createQuery("SELECT f FROM Favorites f WHERE f.favoritesPK.routename = :routename AND f.favoritesPK.username = :username");
            q.setParameter("routename", routename);
            q.setParameter("username", username);
            Favorites favorite = (Favorites) q.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("Favorite non presente");
            return "no";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            FavoritesPK key = new FavoritesPK(routename, username);
            super.remove(super.find(key));
            return "yes";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "no";
        }
    }

    @GET
    @Path("{routename}/{username}")
    @Produces({"application/json"})
    public Favorites find(@PathParam("routename") String routename, @PathParam("username") String username) {
        FavoritesPK key = new FavoritesPK(routename, username);
        return super.find(key);
    }

    @GET
    @Path("{username}")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public List<Favorites> findAll(@PathParam("username") String username) {
        Query q = getEntityManager().createQuery("SELECT f FROM Favorites f WHERE f.favoritesPK.username = :username");
        q.setParameter("username", username);
        return q.getResultList();
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
