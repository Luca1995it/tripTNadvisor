/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notify;

import DataBase.DBManager;
import DataBase.Ristorante;
import DataBase.Utente;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public class RichiestaRistorante extends Notifica {

    private final Utente utente;
    private final Ristorante ristorante;

    /**
     * Crea un oggetto di tipo RichiestaRistorante, che serve per notificare e
     * far decidere ad un amministratore se il ristorante va assegnato ad un
     * utente
     *
     * @param manager collegamento al DBManager per poter effetturare operazioni
     * sul DB
     * @param id id sul DB
     * @param data data di creazione
     * @param utente utente che chiede il possesso di un ristorante
     * @param ristorante ristorante di cui l'utente chiede il possesso
     */
    public RichiestaRistorante(int id, Date data, Utente utente, Ristorante ristorante, DBManager manager) {
        super(id, data, manager);
        this.ristorante = ristorante;
        this.utente = utente;
    }

    @Override
    public boolean rifiuta() {
        return done();
    }

    @Override
    public boolean accetta() {
        manager.linkRistorante(ristorante, utente);
        return done();
    }

    @Override
    public boolean done() {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("delete from richiestaristorante where id = ?");
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
        String res = "L'utente " + utente.getNomeCognome() + " ha chiesto il possesso del ristorante " + ristorante.getNome();
        return res;
    }

    @Override
    public String getFotoPath() {
        return ristorante.getFoto().get(0).getFotopath();
    }

}
