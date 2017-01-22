package DataBase;

import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

/**
 *
 * @author lucadiliello
 */
public class Times implements Serializable {
    private final int id;
    private final Time apertura;
    private final Time chiusura;
    private final Days day;

    public int getId() {
        return id;
    }
    
    public Time getChiusura() {
        return chiusura;
    }
    
    public Time getApertura() {
        return apertura;
    }

    Times(int id, Time apertura, Time chiusura, Days day){
        this.day = day; 
        this.apertura = apertura;
        this.chiusura = chiusura;
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.apertura);
        hash = 47 * hash + Objects.hashCode(this.chiusura);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Times other = (Times) obj;
        if (!Objects.equals(this.apertura, other.apertura)) {
            return false;
        }
        return Objects.equals(this.chiusura, other.chiusura);
    }    
    
    @Override
    public String toString(){
        String a = "", b = "";
        if(apertura.getMinutes()<10) a = "0";
        if(chiusura.getMinutes()<10) b = "0";
        return apertura.getHours() + ":" + apertura.getMinutes() + a +" - " + chiusura.getHours() + ":" + chiusura.getMinutes() + b;
    }
}

