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
public class SegnalaFotoRecensione extends Notifica {

    private final Recensione recensione;

    /**
     * Crea un nuovo oggetto di tipo SegnalaFotoRecensione, che serve ad un
     * utente ristoratore di segnalare una foto di una recensione non consona
     *
     * @param manager collegamento al DBManager per operare sul DB
     * @param id id sul DB
     * @param data data di creazione
     * @param recensione recensione nella quale è contenuta la foto da segnalare
     */
    public SegnalaFotoRecensione(int id, Date data, Recensione recensione, DBManager manager) {
        super(id, data, manager);
        this.recensione = recensione;
    }

    @Override
    public boolean rifiuta() {
        return done();
    }

    @Override
    public boolean accetta() {
        return done() && recensione.removeFoto();
    }

    @Override
    public boolean done() {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("delete from segnalafotorecensione where id = ?");
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
        String res = "Rimozione foto recensione: L'utente ristoratore " + recensione.getRistorante().getUtente().getNomeCognome()
                + " proprietario del ristorante " + recensione.getRistorante().getNome()
                + " ha chiesto la rimozione della seguente foto dalla recensione "
                + recensione.getTitolo() + "\n" + recensione.getTesto();
        return res.length() > not_length ? res.substring(0, not_length-3) + "..." : res;
    }

    @Override
    public String getFotoPath() {
        return recensione.getFotoPath();
    }

}
