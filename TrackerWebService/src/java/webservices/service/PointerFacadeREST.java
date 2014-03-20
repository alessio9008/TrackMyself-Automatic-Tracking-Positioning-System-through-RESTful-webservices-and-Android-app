/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.service;

import java.util.LinkedList;
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
import webservices.Pointer;
import webservices.PointerPK;

/**
 *
 * @author Alessio Oglialoro, Daniele Saitta
 */
@Stateless
@Path("webservices.pointer")
public class PointerFacadeREST extends AbstractFacade<Pointer> {

    @PersistenceContext(unitName = "TrackerWebServicePU")
    private EntityManager em;

    private PointerPK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;timestampinsert=timestampinsertValue;username=usernameValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        webservices.PointerPK key = new webservices.PointerPK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> timestampinsert = map.get("timestampinsert");
        if (timestampinsert != null && !timestampinsert.isEmpty()) {
            key.setTimestampinsert(new java.lang.Long(timestampinsert.get(0)));
        }
        java.util.List<String> username = map.get("username");
        if (username != null && !username.isEmpty()) {
            key.setUsername(username.get(0));
        }
        return key;
    }

    public PointerFacadeREST() {
        super(Pointer.class);
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String createPointer(List<Pointer> entry) {
        String result = "no";
        for (Pointer entity : entry) {
            try {
                Query q = getEntityManager().createQuery("SELECT p FROM Pointer p WHERE p.pointerPK.timestampinsert = :timestampinsert AND p.pointerPK.username = :username");
                q.setParameter("timestampinsert", entity.getPointerPK().getTimestampinsert());
                q.setParameter("username", entity.getPointerPK().getUsername());
                Pointer point = (Pointer) q.getSingleResult();
            } catch (NoResultException ex) {
                System.out.println("Pointer non presente");
                super.create(entity);
                result = "yes";
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @GET
    @Path("{user}/{from}/{to}")
    @Produces({"application/json"})
    public List<Pointer> findRange(@PathParam("user") String user, @PathParam("from") Long from, @PathParam("to") Long to) {
        Query q = getEntityManager().createQuery("SELECT p FROM Pointer p WHERE p.pointerPK.username = :username AND p.pointerPK.timestampinsert >= :from AND p.pointerPK.timestampinsert <= :to");
        q.setParameter("username", user);
        q.setParameter("from", from.longValue());
        q.setParameter("to", to.longValue());
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
