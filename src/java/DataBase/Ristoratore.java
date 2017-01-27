/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import Notify.Notifica;
import Notify.NuovaFoto;
import Notify.NuovaRecensione;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public class Ristoratore extends Utente {

    private final boolean attivato;
    private final boolean accettato;

    public Ristoratore(int id, String nome, String cognome, String email, String avpath, boolean attivato, boolean accettato, DBManager manager) {
        super(id, nome, cognome, email, avpath, manager);
        this.accettato = accettato;
        this.attivato = attivato;
    }
    
    @Override
    public ArrayList<Notifica> getNotifiche() {
        if(manager == null) return new ArrayList<>();
        ArrayList<Notifica> res = new ArrayList<>();
        res.addAll(getNuovaFotoNotifiche());
        res.addAll(getNuovaRecensioneNotifiche());
        Comparator<Notifica> c = (Notifica o1, Notifica o2) -> {
            if(o1.getData().after(o2.getData())) return 1;
            else if(o1.getData().before(o2.getData())) return -1;
            else return 0;
        };
        res.sort(c);
        return res;
    }
    
    /*
    * Riceve tutte le notifiche di avvenuta aggiunta foto ad uno dei ristorante dell'utente
     */
    ArrayList<NuovaFoto> getNuovaFotoNotifiche() {
        ArrayList<NuovaFoto> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select nuovafoto.* from "
                    + "(select myrist.id from (select myfoto.id, ristorante.ID_UTENTE from "
                    + "(select foto.id, foto.id_rist from nuovafoto, foto where nuovafoto.ID_FOTO = foto.id) "
                    + "as myfoto, ristorante where myfoto.id_rist = ristorante.id) as myrist, utente where myrist.id_utente = utente.id and utente.id = ?) "
                    + "as id, nuovafoto where id.id = nuovafoto.ID_foto");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new NuovaFoto(rs.getInt("id"), rs.getDate("data"), manager.getFoto(rs.getInt("id_foto")), manager));
            }
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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    /*
    * Riceve tutte le notifiche di avvenuta aggiunta recensione ad uno dei ristorante dell'utente
     */
    ArrayList<NuovaRecensione> getNuovaRecensioneNotifiche() {
        ArrayList<NuovaRecensione> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select nuovarecensione.* from (select myrist.id from "
                    + "(select myrec.id, ristorante.ID_UTENTE from "
                    + "(select recensione.id, recensione.id_rist from nuovarecensione, recensione where nuovarecensione.id_rec = recensione.id) "
                    + "as myrec, ristorante where myrec.id_rist = ristorante.id)"
                    + " as myrist, utente where myrist.id_utente = utente.id and utente.id = ?) "
                    + "as id, nuovarecensione where id.id = nuovarecensione.id_rec");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new NuovaRecensione(rs.getInt("id"), rs.getDate("data"), manager.getRecensione(rs.getInt("id_rec")), manager));
            }
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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    /**
     * Per ottenere la lista dei ristoranti posseduti da un utente
     *
     * @return Un ArrayList dei ristoranti dell'utente
     */
    public ArrayList<Ristorante> getRistoranti() {
        ArrayList<Ristorante> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            System.out.println(manager + " " + manager.con);
            stm = manager.con.prepareStatement("select * from Ristorante where id_utente = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linksito"), rs.getString("fascia"), manager.getCucina(rs.getInt("id")), manager.getUtente(rs.getInt("id_utente")), rs.getInt("visite"), manager.getLuogo(rs.getInt("id_luogo")), manager));
            }
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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    @Override
    public boolean isActivate() {
        return attivato;
    }

    @Override
    public boolean isAccettato() {
        return accettato;
    }
}
