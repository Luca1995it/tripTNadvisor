/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import Notify.CommentoRecensione;
import Notify.Notifica;
import Notify.RichiestaRistorante;
import Notify.SegnalaFotoRecensione;
import Notify.SegnalaFotoRistorante;
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
public class Amministratore extends Utente {  
    
    public Amministratore(int id, String nome, String cognome, String email, String avpath, DBManager manager) {
        super(id, nome, cognome, email, avpath, manager);
    }

    @Override
    public ArrayList<Notifica> getNotifiche() {
        ArrayList<Notifica> res = new ArrayList<>();
        res.addAll(getCommentoRecensioneNotifiche());
        res.addAll(getReclamaRistoranteNotifiche());
        res.addAll(getSegnalaFotoRistoranteNotifiche());
        res.addAll(getSegnalaFotoRecensioneNotifiche());
        Comparator<Notifica> c = (Notifica o1, Notifica o2) -> {
            if(o1.getData().after(o2.getData())) return 1;
            else if(o1.getData().before(o2.getData())) return -1;
            else return 0;
        };
        res.sort(c);
        return res;
    }

    /*
    * Per ricevere tutte le notifiche di avvenuto commento a recensione
     */
    ArrayList<CommentoRecensione> getCommentoRecensioneNotifiche() {
        ArrayList<CommentoRecensione> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select * from rispostarecensione");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new CommentoRecensione(rs.getInt("id"), manager.getRecensione(rs.getInt("id_rec")), rs.getString("commento"), rs.getDate("data"), manager));
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
    * Per ricevere tutte le notifiche di avvenuta richiesta di possesso ristorante da parte di un utente
     */
    ArrayList<RichiestaRistorante> getReclamaRistoranteNotifiche() {
        ArrayList<RichiestaRistorante> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select * from richiestaristorante");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new RichiestaRistorante(rs.getInt("id"), rs.getDate("data"), manager.getUtente(rs.getInt("id_utente")), manager.getRistorante(rs.getInt("id_rist")), manager));
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
    * Per ricevere tutte le notifiche di avvenuta segnalazione foto di un ristorante da parte del proprietario
     */
    ArrayList<SegnalaFotoRistorante> getSegnalaFotoRistoranteNotifiche() {
        ArrayList<SegnalaFotoRistorante> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select * from segnalafotoristorante");
            rs = stm.executeQuery();
            while (rs.next()) {
                Foto foto = manager.getFoto(rs.getInt("id_foto"));//DBManager manager, int id, Date data, Ristorante ristorante, Foto foto
                res.add(new SegnalaFotoRistorante(rs.getInt("id"), rs.getDate("data"), manager.getRistorante(foto.getRistorante().getId()), foto, manager));
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
    * Per ricevere tutte le notifiche di avvenuta segnalazione della foto di una recensione da parte del proprietario del ristorante a cui riferisce la recensione
     */
    ArrayList<SegnalaFotoRecensione> getSegnalaFotoRecensioneNotifiche() {
        ArrayList<SegnalaFotoRecensione> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select * from segnalafotorecensione");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new SegnalaFotoRecensione(rs.getInt("id"), rs.getDate("data"), manager.getRecensione(rs.getInt("id_rec")), manager));
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
        return true;
    }

    @Override
    public boolean isAccettato() {
        return true;
    }

}
