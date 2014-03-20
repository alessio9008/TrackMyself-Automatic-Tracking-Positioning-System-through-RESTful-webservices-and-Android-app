/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.service;

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
import webservices.User;

/**
 *
 * @author Alessio Oglialoro, Daniele Saitta
 */
@Stateless
@Path("webservices.user")
public class UserFacadeREST extends AbstractFacade<User> {

    @PersistenceContext(unitName = "TrackerWebServicePU")
    private EntityManager em;

    public UserFacadeREST() {
        super(User.class);
    }

    @POST
    @Consumes({"application/json"})
    @Produces("application/json")
    public String createUser(User entity) {
        try {
            Query q = getEntityManager().createQuery("SELECT u FROM User u WHERE u.username = :username");
            q.setParameter("username", entity.getUsername());
            User user = (User) q.getSingleResult();
            if(user!=null) return "no";
        }catch(NoResultException ex){ 
            System.out.println("User non presente");
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

    @GET
    @Path("{id}/{password}")
    @Produces("text/plain")
    public String find(@PathParam("id") String id, @PathParam("password") String password) {
        User usr = super.find(id);
        if (usr != null) {
            if (password.equals(usr.getPassword())) {
                return "yes";
            }
        }
        return "no";
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
