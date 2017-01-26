/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public class Foto implements Serializable {
    transient private final DBManager manager;
    /**
     * Creazione dell'oggetto foto
     * @param id id della foto su DB
     * @param fotopath path della foto riferito a contextpath del progetto
     * @param descr breve descrizione della foto
     * @param data data di pubblicazione
     * @param utente utente che ha pubblicato questa foto
     * @param ristorante ristorante sulla quale è pubblicata questa foto
     * @param manager per collegamente a database
     */
    public Foto(int id, String fotopath, String descr, Date data, Utente utente, Ristorante ristorante, DBManager manager) {
        this.manager = manager;
        this.id = id;
        this.fotopath = fotopath;
        this.descr = descr;
        this.data = data;
        this.ristorante = ristorante;
        this.utente = utente;
    }


    public int getId() {
        return id;
    }

    public String getFotopath() {
        return fotopath;
    }

    public String getDescr() {
        return descr;
    }

    public Date getData() {
        return data;
    }

 
    private final int id;
    private final String fotopath;
    private final String descr;
    private final Date data;
    private final Utente utente;
    private final Ristorante ristorante;

    public Ristorante getRistorante() {
        return ristorante;
    }
    
    public boolean justSegnalato() {
        if(id == -1) return true;
        PreparedStatement stm = null;
        ResultSet rs;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("select * from segnalafotoristorante where id_foto = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            res = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }
    
    /**
     * Per ottenere l'utente proprietario di questa foto, cioè colui che l'ha caricata e pubblicata
     * @return l'utente proprietario della foto
     */
    public Utente getUtente(){
        return utente;
    }
    
}
