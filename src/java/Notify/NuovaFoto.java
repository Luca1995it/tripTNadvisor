/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notify;

import DataBase.DBManager;
import DataBase.Foto;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucadiliello
 */
public class NuovaFoto extends Notifica {

    private final Foto foto;

    /**
     * Crea un nuovo oggetto di tipo  NuovaFoto, per notificare ad un Ristoratore l'aggiunta di una foto ad un suo ristorante
     * @param manager collegamento al DBManager per eseguire operazioni sul db
     * @param id id della notifica su db
     * @param data data di creazione della notifica
     * @param foto Foto in allegato alla notifica
     */
    public NuovaFoto(int id, Date data, Foto foto, DBManager manager) {
        super(id, data, manager);
        this.foto = foto;
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
            System.out.println("Cancello con id: " + id);
            stm = manager.con.prepareStatement("delete from nuovafoto where id = ?");
            stm.setInt(1, id);
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
        String res = "Nuova foto: L'utente " + foto.getUtente() + " ha aggiunto una nuova foto al tuo ristorante " + foto.getRistorante().getNome();
        return res.length() > 40 ? res.substring(0, 40) : res;
    }
    
    @Override
    public String getFotoPath(){
        return foto.getFotopath();
    }

}
