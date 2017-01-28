/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notify;

import DataBase.DBManager;
import DataBase.Recensione;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucadiliello
 */
public class NuovaRecensione extends Notifica {

    private final Recensione recensione;
    
    /**
     * Crea un nuovo oggetto di tipo NuovaRecensione, che servirà per avvisare un Utente che un suo ristorante è stato recensito
     * @param manager collegamento al DBManager
     * @param id id su DB
     * @param data data di creazione della notifica
     * @param recensione recensione alla quale si riferisce
     */
    public NuovaRecensione(int id, Date data, Recensione recensione, DBManager manager) {
        super(id, data, manager);
        this.recensione = recensione;
    }

    @Override
    public boolean rifiuta() {
        return done();
    }

    @Override
    public boolean accetta() {
        return done();
    }

    @Override
    public boolean done() {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("delete from nuovarecensione where id = ?");
            stm.setInt(1, getId());
            stm.executeUpdate();
            res = true;
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


    @Override
    public String toString() {
        String res = "Nuova Recensione: L'utente " + recensione.getUtente().getNomeCognome() + " ha aggiunto una nuova recensione al tuo ristorante " + recensione.getRistorante().getNome() + "\n"
                + "Titolo: " + recensione.getTitolo()
                + "Testo: " + recensione.getTesto();
        return res;
    }

    @Override
    public String getFotoPath() {
        return recensione.getFotoPath();
    }
    
}
