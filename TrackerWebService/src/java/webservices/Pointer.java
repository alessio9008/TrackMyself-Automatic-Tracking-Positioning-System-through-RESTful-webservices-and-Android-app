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
@Table(name = "pointer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pointer.findAll", query = "SELECT p FROM Pointer p"),
    @NamedQuery(name = "Pointer.findByTimestampinsert", query = "SELECT p FROM Pointer p WHERE p.pointerPK.timestampinsert = :timestampinsert"),
    @NamedQuery(name = "Pointer.findByUsername", query = "SELECT p FROM Pointer p WHERE p.pointerPK.username = :username"),
    @NamedQuery(name = "Pointer.findByLatitude", query = "SELECT p FROM Pointer p WHERE p.latitude = :latitude"),
    @NamedQuery(name = "Pointer.findByLongitude", query = "SELECT p FROM Pointer p WHERE p.longitude = :longitude")})
public class Pointer implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PointerPK pointerPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "latitude")
    private double latitude;
    @Basic(optional = false)
    @NotNull
    @Column(name = "longitude")
    private double longitude;

    public Pointer() {
    }

    public Pointer(PointerPK pointerPK) {
        this.pointerPK = pointerPK;
    }

    public Pointer(PointerPK pointerPK, double latitude, double longitude) {
        this.pointerPK = pointerPK;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Pointer(long timestampinsert, String username) {
        this.pointerPK = new PointerPK(timestampinsert, username);
    }

    public PointerPK getPointerPK() {
        return pointerPK;
    }

    public void setPointerPK(PointerPK pointerPK) {
        this.pointerPK = pointerPK;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pointerPK != null ? pointerPK.hashCode() : 0);
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
        final Pointer other = (Pointer) obj;
        if (!Objects.equals(this.pointerPK, other.pointerPK)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "Pointer{" + "pointerPK=" + pointerPK + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

   
    
}
