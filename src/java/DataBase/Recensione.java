/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public class Recensione implements Serializable{

    transient private final DBManager manager;
    private final int id;
    private final String titolo;
    private final String testo;
    private final Date data;
    private String commento;
    private final Ristorante ristorante;
    private final Utente utente;
    private String fotoPath;

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getTesto() {
        return testo;
    }

    public Date getData() {
        return data;
    }

    public String getCommento() {
        return commento;
    }

    public Ristorante getRistorante() {
        return ristorante;
    }

    public Utente getUtente() {
        return utente;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    

    /**
     * Costruttore di una Recensione
     * @param id id della recensione sul DB
     * @param ristorante ristorante a cui appartiene la recensione
     * @param utente utente che ha scritto la recensione
     * @param titolo titolo della recensione
     * @param testo testo o corpo della recensione
     * @param data data in cui è stata pubblicata la recensione
     * @param commento commento scritto dal proprietario del ristorante a questa recensione
     * @param fotoPath path della foto di questa recensione
     * @param manager collegamento al DBManager per utilizzare il DB
     */
    public Recensione(int id, String titolo, String testo, Date data, String commento, String fotoPath, Ristorante ristorante, Utente utente, DBManager manager) {
        this.id = id;
        this.titolo = titolo;
        this.testo = testo;
        this.data = data;
        this.commento = commento;
        this.fotoPath = fotoPath;
        this.manager = manager;
        this.utente = utente;
        this.ristorante = ristorante;
    }



    /**
     * Aggiunge un commento alla recensione, scritto dal proprietario del
     * ristorante a cui è riferica
     *
     * @param commento il commento da aggiungere
     * @return true se il commento è stato inserito/aggiuornato con successo,
     * false altrimenti
     */
    public boolean addComment(String commento) {
        if(commento == null) return false;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("UPDATE RECENSIONE SET commento = ? WHERE id = ?");
            stm.setString(1, commento);
            stm.setInt(2, getId());
            stm.executeUpdate();
            this.commento = commento;
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

    public boolean justSegnalato() {
        if(fotoPath.equals("/defaultItem/rec_default.png")) return true;
        PreparedStatement stm = null;
        ResultSet rs;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("select * from segnalafotorecensione where id_rec = ?");
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
     * Aggiunge/Aggiorna la foto di questa recensione
     *
     * @param pathFoto il path della foto da aggiungere/aggiornare
     * @return true se la foto è stata aggiunta/aggiornata con successo, false
     * altrimenti
     */
    public boolean addFoto(String pathFoto) {
        if(pathFoto == null) return false;
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("UPDATE RECENSIONE SET fotoPath = ? WHERE id = ?");
            stm.setString(1, pathFoto);
            stm.setInt(2, getId());
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

    /**
     * Elimina la foto di questa recensione
     *
     * @return true se la foto è stata eliminata con successo, false altrimenti
     */
    public boolean removeFoto() {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("UPDATE RECENSIONE SET fotoPath = ? WHERE id = ?");
            stm.setString(1, manager.defaultFolder + "/rec_default.png");
            stm.setInt(2, getId());
            stm.executeUpdate();
            fotoPath = manager.defaultFolder + "/rec_default.png";
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

    /**
     * Per ottenere la valutazione di questa recensione come media delle
     * valutazioni lasciate dagli altri utenti
     *
     * @return float tra 0 e 5 che indica la media delle valutazioni lasciate a
     * questa recensione
     */
    public float getMediaVoti() {
        PreparedStatement stm = null;
        float res = 0;
        try {
            stm = manager.con.prepareStatement("select rating from votorec where id_rec = ?");
            stm.setInt(1, getId());
            ResultSet rs = stm.executeQuery();
            int somma = 0;
            int count = 0;
            while (rs.next()) {
                somma += rs.getInt("rating");
                count++;
            }
            res = count != 0 ? ((float) somma) / count : 0;
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
     * Per aggiungere un voto alla recensione
     *
     * @param utente utente che vota la recensione
     * @param voto intero da 0 a 5
     * @return
     */
    public boolean addVoto(Utente utente, int voto) {
        if(utente == null) return false;
        PreparedStatement stm = null;
        boolean res = false;
        Date current = Date.valueOf(LocalDate.now());
        try {
            stm = manager.con.prepareStatement("insert into votorec (id_utente,id_rec,rating,data) values (?,?,?,?)");
            stm.setInt(1, utente.getId());
            stm.setInt(2, getId());
            stm.setInt(3, voto);
            stm.setDate(4, current);
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
    /**
     * Per controllare se la foto è già stata commentata
     * @return true se è già stata commentata, false altrimenti
     */
    public boolean esisteCommento() {

        return commento != null;
    }
}
