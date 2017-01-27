/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import Notify.Notifica;
import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luca
 */
public abstract class Utente implements Serializable {

    private final int id;
    private String nome;
    private String cognome;
    private String email;
    private String avpath;
    transient protected final DBManager manager;

    public Utente(int id, String nome, String cognome, String email, String avpath, DBManager manager) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.avpath = avpath;
        this.manager = manager;
    }

    /**
     * Funzione che raccoglie tutte le notifiche destinate all'utente
     *
     * @return Un ArrayList di Notifica che non sono ancora state
     * confermate/eliminate
     */
    public abstract ArrayList<Notifica> getNotifiche();

    public boolean updateNome(String nome) {
        if (nome == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update utente set nome = ? where id = ?");
            stm.setString(1, nome);
            stm.setInt(2, id);
            if (stm.executeUpdate() == 1) {
                this.nome = nome;
                res = true;
            } else {
                res = false;
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
        }
        return res;
    }

    public boolean updateCognome(String cognome) {
        if (cognome == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update utente set cognome = ? where id = ?");
            stm.setString(1, cognome);
            stm.setInt(2, id);
            if (stm.executeUpdate() == 1) {
                this.cognome = cognome;
                res = true;
            } else {
                res = false;
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
        }
        return res;
    }

    public boolean updateEmail(String email) {
        if (email == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update utente set email = ? where id = ?");
            stm.setString(1, email);
            stm.setInt(2, id);
            if (stm.executeUpdate() == 1) {
                this.email = email;
                res = true;
            } else {
                res = false;
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
        }
        return res;
    }

    public boolean updateAvpath(String avpath) {
        if (avpath == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("UPDATE utente SET avpath = ? where id = ?");
            stm.setString(1, avpath);
            stm.setInt(2, id);
            if (stm.executeUpdate() == 1) {
                this.avpath = avpath;
                res = true;
            } else {
                res = false;
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
        }
        return res;
    }

    public boolean updatePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("UPDATE utente SET password = ? where id = ? and password = ?");
            stm.setString(1, newPassword);
            stm.setInt(2, id);
            stm.setString(3, oldPassword);
            res = stm.executeUpdate() == 1;
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
     * Per ottenere la password di un utente
     *
     * @return
     */
    public String getPassword() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        String res = null;
        try {
            stm = manager.con.prepareStatement("select password from Utente where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getString("password");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stm != null) {
                try {
                    stm.close();

                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();

                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    public int numeroRecensioni() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        int res = 0;
        try {
            stm = manager.con.prepareStatement("select count(*) as tot from recensione where id_utente = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getInt("tot");
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

    public boolean justSegnalataFoto(Foto foto) {
        if (foto == null) {
            return false;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("select * from segnalafotoristorante where id_foto = ?");
            stm.setInt(1, foto.getId());
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

    public int getPosizioneClassifica() {
        if (this.isAmministratore()) {
            return -1;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        int res = 1;
        try {
            stm = manager.con.prepareStatement("select utente.id as idU from (select avg(voti.rate) as mid, recensione.ID_UTENTE as "
                    + "id from (select recensione.id as id, avg(rating) as rate from recensione left join votorec on "
                    + "recensione.id = votorec.ID_REC group by recensione.id) as voti, recensione where voti.id = recensione.ID "
                    + "group by recensione.ID_UTENTE) as res right join (select * from utente where amministratore = false) as utente"
                    + " on res.id = utente.ID order by mid desc nulls last");

            rs = stm.executeQuery();
            boolean found = false;
            while (rs.next() && !found) {
                if (rs.getInt("idU") == getId()) {
                    found = true;
                } else {
                    res++;
                }
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
     * Calcola la reputazione di un utente sulla media dei voti delle recensioni
     * che ha lasciato
     *
     * @return un float tra 0 e 5 alle recensioni di questo utente
     */
    public float getReputazione() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        float res = -1;
        try {
            stm = manager.con.prepareStatement("select mid*1.0 as media from (select avg(voti.rate) as mid, recensione.ID_UTENTE as id "
                    + "from (select recensione.id as id, avg(rating) as rate from recensione left join votorec on recensione.id = votorec.ID_REC "
                    + "group by recensione.id) as voti, recensione where voti.id = recensione.ID group by recensione.ID_UTENTE) as res right join "
                    + "(select * from utente where amministratore = false) as utente on res.id = utente.ID where utente.ID = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getFloat("media");
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
     *
     * @param ristorante ristorante per cui ci vuole controllare se l'utente ha
     * già rilasciato una recensione
     * @return true se questo utente ha già recensito questo ristorante, false
     * altrimenti
     */
    public boolean justRecensito(Ristorante ristorante) {
        if (ristorante == null) {
            return true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = true;
        try {
            stm = manager.con.prepareStatement("select * from recensione where id_rist = ? AND id_utente = ?");
            stm.setInt(1, ristorante.getId());
            stm.setInt(2, getId());
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
     * Serve a verificare se un utente ha già votato un ristorante oggi
     *
     * @param ristorante il ristorante in questione
     * @return true se l'utente ha già votato quel ristorante oggi, false
     * altrimenti
     */
    public boolean justVotatoOggi(Ristorante ristorante) {
        if (ristorante == null) {
            return true;
        }
        PreparedStatement stm = null;
        ResultSet rs;
        boolean res = false;
        Date d;
        try {
            Date now = new Date(System.currentTimeMillis());
            stm = manager.con.prepareStatement("select data from votorist where id_utente = ? AND id_rist = ? order by data desc { limit 1 }");
            stm.setInt(1, getId());
            stm.setInt(2, ristorante.getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                d = rs.getDate("data");
                res = d.getDay() == now.getDay() && d.getMonth() == now.getMonth() && d.getYear() == now.getYear();
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
        }
        return res;
    }

    /**
     * Serve a verificare se un utente ha già votato un ristorante oggi
     *
     * @param recensione
     * @return true se l'utente ha già votato quel ristorante oggi, false
     * altrimenti
     */
    public boolean justVotato(Recensione recensione) {
        if (recensione == null) {
            return true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = true;
        Date d;
        try {
            stm = manager.con.prepareStatement("select * from votorec where id_utente = ? AND id_rec = ?");
            stm.setInt(1, id);
            stm.setInt(2, recensione.getId());
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
     * Questa funzione controlla se l'utente è proprietario di un ristorante
     *
     * @param ristorante ristorante
     * @return true se l'utente è proprietario del ristorante, false altrimentiw
     */
    public boolean proprietario(Ristorante ristorante) {
        if (ristorante == null) {
            return false;
        } else if (ristorante.getUtente() == null) {
            return false;
        } else {
            return ristorante.getUtente().equals(this);
        }
    }

    /**
     * Serve a verificare se un utente è proprietario di una recensione
     *
     * @param recensione
     * @return
     */
    public boolean proprietario(Recensione recensione) {
        if (recensione == null) {
            return false;
        } else {
            return recensione.getUtente().equals(this);
        }
    }

    /**
     * Per controllare se un utente è attivato, ovvero ha confermato la sua
     * registrazione via mail Un amministratore è sempre verificato, quindi per
     * utenti amministratori verrà ritornato sempre true
     *
     * @return true se l'utente è attivato, false altrimenti
     */
    public abstract boolean isActivate();

    public abstract boolean isAccettato();

    /**
     * Per controllare se un utente è attivato, ovvero ha confermato la sua
     * registrazione via mail Un amministratore è sempre verificato, quindi per
     * utenti amministratori verrà ritornato sempre true
     *
     * @return true se l'utente è attivato, false altrimenti
     */
    public boolean isAccepted() {
        if (this.getClass() == Amministratore.class) {
            return true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("select accettato from Utente where id = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getBoolean("accettato");
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

    public boolean accettaPrivacy() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update utente set accettato = true where id = ?");
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
     *
     * @param nome nome completo del ristorante
     * @param desc descrizione del ristorante
     * @param linkSito link al sito web del ristorante
     * @param fascia fascia del ristorante ("Economica","Normale","Lussuoso")
     * @param spec
     * @param address indirizzo del ristorante
     * @param fotoPath prima foto del ristorante (sarà possibile aggiungerne
     * altre)
     * @param fotoDescr descrizione della prima foto
     * @return true se la registrazione del ristorante sul db ha avuto successo,
     * false altrimenti
     */
    public boolean addRistorante(String nome, String desc, String linkSito, String fascia, String[] spec, String address, String fotoPath, String fotoDescr) {
        System.out.println("Fotopath: " + fotoPath);
        linkSito = manager.adjustLink(linkSito);
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("INSERT INTO Ristorante (nome,descr,linksito,fascia) VALUES (?,?,?,?)");
            stm.setString(1, nome);
            stm.setString(2, desc);
            stm.setString(3, linkSito);
            stm.setString(4, fascia);
            stm.executeUpdate();

            stm = manager.con.prepareStatement("select id from Ristorante where nome = ? AND linkSito = ?");
            stm.setString(1, nome);
            stm.setString(2, linkSito);
            rs = stm.executeQuery();
            if (rs.next()) {
                Ristorante rist = manager.getRistorante(rs.getInt("id"));
                rist.addFoto(fotoPath, fotoDescr, this);
                rist.updateLuogo(address);
                for (String x : spec) {
                    rist.addCucina(x);
                }
                res = true;
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
        manager.updateAutocomplete();
        return res;
    }

    @Override
    public String toString() {
        return getNomeCognome();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Utente other = (Utente) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.cognome, other.cognome)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return Objects.equals(this.avpath, other.avpath);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.nome);
        hash = 17 * hash + Objects.hashCode(this.email);
        return hash;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeCognome() {
        return nome + " " + cognome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getAvpath() {
        return avpath;
    }

    /**
     * Per sapere se un utente è di tipo registrato
     *
     * @return true se l'utente è di tipo Registrato, false altrimenti
     */
    public boolean isRegistrato() {
        return this instanceof Registrato;
    }

    /**
     * Per sapere se un utente è di tipo Amministratore
     *
     * @return true se l'utente è di tipo Amministratore, false altrimenti
     */
    public boolean isAmministratore() {
        return this instanceof Amministratore;
    }

    /**
     * Per sapere se un utente è di tipo Ristoratore
     *
     * @return true se l'utente è di tipo Ristoratore, false altrimenti
     */
    public boolean isRistoratore() {
        return this instanceof Ristoratore;
    }

    /**
     * Per sapere se un utente è uno registrato, amministratore o ristoratore
     *
     * @return true se l'utente è loggato
     */
    public boolean isLogged() {
        return isRegistrato() || isAmministratore() || isRistoratore();
    }
}
