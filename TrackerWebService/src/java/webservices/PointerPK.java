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
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Alessio Oglialoro, Daniele Saitta
 */
@Embeddable
public class PointerPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "timestampinsert")
    private long timestampinsert;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "username")
    private String username;

    public PointerPK() {
    }

    public PointerPK(long timestampinsert, String username) {
        this.timestampinsert = timestampinsert;
        this.username = username;
    }

    public long getTimestampinsert() {
        return timestampinsert;
    }

    public void setTimestampinsert(long timestampinsert) {
        this.timestampinsert = timestampinsert;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) timestampinsert;
        hash += (username != null ? username.hashCode() : 0);
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
        final PointerPK other = (PointerPK) obj;
        if (this.timestampinsert != other.timestampinsert) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "PointerPK{" + "timestampinsert=" + timestampinsert + ", username=" + username + '}';
    }

    
    
}
