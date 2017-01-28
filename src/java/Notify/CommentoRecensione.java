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
 * @author Luca
 */
public class CommentoRecensione extends Notifica {

    private final Recensione rec;
    private final String commento;

    /**
     * Crea un nuovo oggetto di tipo CommentoRecensione, che servirà per
     * rifiutare o confermare l'aggiunta di un commento da parte di un
     * ristoratore ad una recensione lasciata da un Utente
     *
     * @param manager collegamento al DBManager per accedere al DB
     * @param id id sul db
     * @param rec recensione alla quale si riferisce la notifica
     * @param commento commento che si intende aggiungere
     * @param data data di creazione della notifica
     */
    public CommentoRecensione(int id, Recensione rec, String commento, Date data, DBManager manager) {
        super(id, data, manager);
        this.commento = commento;
        this.rec = rec;
    }

    @Override
    public String toString() {
        String res = "Commento recensione: L'utente ristoratore " + rec.getRistorante().getUtente().getNomeCognome() + "\n"
                + "voleva aggiungere il commento '" + commento + "' \n"
                + "alla recensione: "
                + rec.getTitolo() + "\n"
                + rec.getTesto() + "\n"
                + "lasciata da " + rec.getUtente().getNomeCognome();
        return res;
    }

    @Override
    public boolean accetta() {
        rec.addComment(commento);
        return done();
    }

    @Override
    public boolean rifiuta() {
        return done();
    }

    @Override
    public boolean done() {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("delete from rispostarecensione where id = ?");
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
    public String getFotoPath() {
        return rec.getFotoPath();
    }

}
