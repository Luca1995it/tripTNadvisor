/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notify;

import DataBase.DBManager;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

/**
 *
 * @author Luca
 */
public abstract class Notifica implements Serializable{

    protected final DBManager manager;
    int id;
    Date data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        hash = 79 * hash + Objects.hashCode(this.data);
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
        final Notifica other = (Notifica) obj;
        return this.id == other.id;
    }
    
    /**
     * Metodo per rifiutare una notifica
     * Rifiuta la richiesta e distrugge la notifica dal DB
     * @return true se è andato tutto bene, false altrimenti
     */
    public abstract boolean rifiuta();

    /**
     * Metodo per accettare una notifica
     * Accetta la richiesta e distrugge la notifica dal DB
     * @return true se è andato tutto bene, false altrimenti
     */
    public abstract boolean accetta();
    
    public abstract boolean done();
    
    @Override
    public abstract String toString();

    /**
     * Per le notifiche che contengono foto ritorna il path relativo
     * @return il path relativo della foto se la notifica contiene una foto, null altrimenti
     */
    public abstract String getFotoPath();
    
    Notifica(int id, Date data, DBManager manager) {
        this.manager = manager;
        this.id = id;
        this.data = data;
    }

    /**
     * Comparazione di due notifiche per data
     * @param o1 notifica 1
     * @param o2 notifica 2
     * @return 1 se la notifica o1 viene dopo nel tempo di o2, -1 se la notifica o1 viene prima nel tempo di o2, 0 altrimenti
     */
    public int compare(Notifica o1, Notifica o2) {
        if (o1.data.after(o2.data)) {
            return 1;
        } else if (o2.data.after(o1.data)) {
            return -1;
        } else {
            return 0;
        }
    }
    
}

