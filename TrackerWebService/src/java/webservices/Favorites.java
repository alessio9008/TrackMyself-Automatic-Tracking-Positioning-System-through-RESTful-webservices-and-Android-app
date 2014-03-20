/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservices;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alessio Oglialoro, Daniele Saitta
 */
@Entity
@Table(name = "favorites")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Favorites.findAll", query = "SELECT f FROM Favorites f"),
    @NamedQuery(name = "Favorites.findByRoutename", query = "SELECT f FROM Favorites f WHERE f.favoritesPK.routename = :routename"),
    @NamedQuery(name = "Favorites.findByUsername", query = "SELECT f FROM Favorites f WHERE f.favoritesPK.username = :username"),
    @NamedQuery(name = "Favorites.findByFromtime", query = "SELECT f FROM Favorites f WHERE f.fromtime = :fromtime"),
    @NamedQuery(name = "Favorites.findByTotime", query = "SELECT f FROM Favorites f WHERE f.totime = :totime")})
public class Favorites implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FavoritesPK favoritesPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fromtime")
    private long fromtime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "totime")
    private long totime;

    public Favorites() {
    }

    public Favorites(FavoritesPK favoritesPK) {
        this.favoritesPK = favoritesPK;
    }

    public Favorites(FavoritesPK favoritesPK, long fromtime, long totime) {
        this.favoritesPK = favoritesPK;
        this.fromtime = fromtime;
        this.totime = totime;
    }

    public Favorites(String routename, String username) {
        this.favoritesPK = new FavoritesPK(routename, username);
    }

    public FavoritesPK getFavoritesPK() {
        return favoritesPK;
    }

    public void setFavoritesPK(FavoritesPK favoritesPK) {
        this.favoritesPK = favoritesPK;
    }

    public long getFromtime() {
        return fromtime;
    }

    public void setFromtime(long fromtime) {
        this.fromtime = fromtime;
    }

    public long getTotime() {
        return totime;
    }

    public void setTotime(long totime) {
        this.totime = totime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (favoritesPK != null ? favoritesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Favorites other = (Favorites) obj;
        if (!Objects.equals(this.favoritesPK, other.favoritesPK)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Favorites{" + "favoritesPK=" + favoritesPK + ", fromtime=" + fromtime + ", totime=" + totime + '}';
    }

    
    
}
