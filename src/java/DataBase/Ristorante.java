/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import static DataBase.DBManager.readJsonFromUrl;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 *
 * @author Luca
 */
public class Ristorante implements Serializable {

    transient private final DBManager manager;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescr() {
        return descr;
    }

    public String getLinksito() {
        return linksito;
    }

    public String getFascia() {
        return fascia;
    }

    public ArrayList<String> getCucina() {
        return cucina;
    }

    public int getVisite() {
        return visite;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    /**
     * Per ottenere il proprietario del ristorante
     *
     * @return
     */
    public Ristoratore getUtente() {
        if (utente == null) {
            return null;
        } else if (utente.isAmministratore()) {
            return (Ristoratore) utente;
        } else {
            return (Ristoratore) utente;
        }
    }

    private final int id;
    private final String nome;
    private final String descr;
    private final String linksito;
    private final String fascia;
    private final ArrayList<String> cucina;
    private final Utente utente;
    private int visite;
    private Luogo luogo;

    /**
     * Crea un nuovo oggetto di tipo Ristorante
     *
     * @param id id del ristorante
     * @param nome
     * @param descr descrizione del ristorante
     * @param linksito link al sito web del ristorante
     * @param fascia fascia di prezzo del ristorante
     * @param cucina tipo di cucina del ristorante
     * @param manager oggetto DBManager per la connessione e l'uso del DB
     * @param utente utente che possiede il ristorante, null altrimenti
     * @param visite numero di visite del ristorante
     * @param luogo
     */
    public Ristorante(int id, String nome, String descr, String linksito, String fascia, ArrayList<String> cucina, Utente utente, int visite, Luogo luogo, DBManager manager) {
        this.luogo = luogo;
        this.id = id;
        this.nome = nome;
        this.descr = descr;
        this.linksito = linksito;
        this.fascia = fascia;
        this.cucina = cucina;
        this.utente = utente;
        this.manager = manager;
        this.visite = visite;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (getClass() != obj.getClass()) {
                return false;
            }
            Ristorante ristorante = (Ristorante) obj;
            if (id != ristorante.id) {
                return false;
            }
            if (!nome.equals(ristorante.nome)) {
                return false;
            }
            if (!linksito.equals(ristorante.linksito)) {
                return false;
            }
            if (!fascia.equals(ristorante.fascia)) {
                return false;
            }
            if (!cucina.equals(ristorante.cucina)) {
                return false;
            }
            if (!descr.equals(ristorante.descr)) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.nome);
        hash = 17 * hash + Objects.hashCode(this.linksito);
        return hash;
    }

    /**
     * Per sapere se il ristorante è già stato reclamato da un utente
     *
     * @return true se il ristorante appartiene ad un utente, false altrimenti
     */
    public boolean reclamato() {
        if (utente != null) {
            return true;
        }

        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = true;
        try {
            stm = manager.con.prepareStatement("select * from richiestaristorante where id_rist = ?");
            stm.setInt(1, id);
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

    public boolean addCucina(String spec) {
        System.out.println("Add:" + spec);
        boolean res = false;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select id from specialita where nome = ?");
            stm.setString(1, spec);
            rs = stm.executeQuery();
            if (rs.next()) {
                stm = manager.con.prepareStatement("insert into specrist (id_spec,id_rist) values (?,?)");
                stm.setInt(1, rs.getInt("id"));
                stm.setInt(2, id);
                stm.executeUpdate();
                res = true;
                cucina.add(spec);
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

    public boolean removeCucina(String spec) {
        System.out.println("Remove:" + spec);
        boolean res = false;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select id from specialita where nome = ?");
            stm.setString(1, spec);
            rs = stm.executeQuery();
            if (rs.next()) {
                stm = manager.con.prepareStatement("delete from specrist where id_spec = ? and id_rist = ?");
                stm.setInt(1, rs.getInt("id"));
                stm.setInt(2, id);
                stm.executeUpdate();
                res = true;
                cucina.remove(spec);
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

    public ArrayList<String> getTutteCucine() {
        return manager.getSpecialita();
    }

    /**
     * Aggiunge una visita al ristorante
     *
     * @return
     */
    public boolean addVisita() {
        boolean res = false;
        PreparedStatement stm = null;
        try {
            stm = manager.con.prepareStatement("update ristorante set visite = visite+1 where id = ?");
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
        visite++;
        return res;
    }

    /**
     *
     * @param nome nuovo nome
     * @return true se i dati sono stati aggiornati correttamente, false
     * altrimenti
     */
    public boolean updateNome(String nome) {
        if (nome == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update ristorante set nome = ? where id = ?");
            stm.setString(1, nome);
            stm.setInt(2, id);
            stm.executeUpdate();
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
        if (res) {
            res = manager.updateAutocomplete();
        }
        return res;
    }

    /**
     *
     * @param descr
     * @return true se i dati sono stati aggiornati correttamente, false
     * altrimenti
     */
    public boolean updateDescrizione(String descr) {
        if (descr == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update ristorante set descr = ? where id = ?");
            stm.setString(1, descr);
            stm.setInt(2, id);
            stm.executeUpdate();
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
        if (res) {
            res = manager.updateAutocomplete();
        }
        return res;
    }

    /**
     *
     * @param linksito
     * @return true se i dati sono stati aggiornati correttamente, false
     * altrimenti
     */
    public boolean updateLinkSito(String linksito) {
        if (linksito == null) {
            return false;
        } else {
            linksito = manager.adjustLink(linksito);
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update ristorante set linksito = ? where id = ?");
            stm.setString(1, linksito);
            stm.setInt(2, id);
            stm.executeUpdate();
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
        if (res) {
            res = manager.updateAutocomplete();
        }
        return res;
    }

    /**
     *
     * @param fascia
     * @return true se i dati sono stati aggiornati correttamente, false
     * altrimenti
     */
    public boolean updateFascia(String fascia) {
        if (fascia == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("update ristorante set fascia = ? where id = ?");
            stm.setString(1, fascia);
            stm.setInt(2, id);
            stm.executeUpdate();
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
        if (res) {
            res = manager.updateAutocomplete();
        }
        return res;
    }

    /**
     * Per settare l'indirizzo del ristorante
     *
     * @param address l'indirizzo del ristorante
     * @return true se la posizione del ristorante è stata impostata
     * correttamente, false altrimenti
     */
    public boolean updateLuogo(String address) {
        if (address == null) {
            return false;
        }
        if (!manager.okLuogo(address)) {
            return false;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {

            String req = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(' ', '+') + "&key=" + manager.googleKey;
            JSONObject json = readJsonFromUrl(req);
            if (json.getString("status").equals("OK")) {

                stm = manager.con.prepareStatement("INSERT INTO Luogo (lat,lng,state,area1,area2,city,street,street_number) VALUES (?,?,?,?,?,?,?,?)");

                JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                stm.setDouble(1, location.getDouble("lat"));
                stm.setDouble(2, location.getDouble("lng"));
                stm.setString(3, faddress.getJSONObject(5).getString("long_name"));
                stm.setString(4, faddress.getJSONObject(3).getString("long_name"));
                stm.setString(5, faddress.getJSONObject(4).getString("long_name"));
                stm.setString(6, faddress.getJSONObject(2).getString("long_name"));
                stm.setString(7, faddress.getJSONObject(1).getString("long_name"));
                stm.setInt(8, Integer.parseInt(faddress.getJSONObject(0).getString("long_name")));

                stm.executeUpdate();

                stm = manager.con.prepareStatement("select * from Luogo where lat = ? AND lng = ?");
                stm.setDouble(1, location.getDouble("lat"));
                stm.setDouble(2, location.getDouble("lng"));
                rs = stm.executeQuery();

                if (rs.next()) {
                    luogo = new Luogo(rs.getInt("id"), rs.getDouble("lat"), rs.getDouble("lng"), rs.getInt("street_number"), rs.getString("street"), rs.getString("city"), rs.getString("area1"), rs.getString("area2"), rs.getString("state"));
                    stm = manager.con.prepareStatement("update ristorante set id_luogo = ? where id = ?");
                    stm.setInt(1, luogo.getId());
                    stm.setInt(2, getId());
                    stm.executeUpdate();
                    res = true;
                }
            }

        } catch (SQLException | JSONException | IOException ex) {
            Logger.getLogger(Ristorante.class.getName()).log(Level.SEVERE, null, ex);
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
        return true;
    }

    /**
     * Funzione che calcola il voto del ristorante come media di tutte le
     * valutazioni lasciate dagli utenti
     *
     * @return un float tra 0 e 5 che valuta la qualità del ristorante
     */
    public float getVoto() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        float res = 0;
        try {
            stm = manager.con.prepareStatement("SELECT avg(1.0 * rating) AS mediavoto FROM votorist WHERE id_rist = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                res = rs.getFloat("mediavoto");
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
     * Funzione che calcola la posizione in classifica instantanea di un
     * ristorante
     *
     * @return la posizione in classifica del ristorante
     */
    public int getPosizioneClassificaPerCitta() {
        if (luogo == null) {
            return -1;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        int res = 1;
        try {
            stm = manager.con.prepareStatement("select avg(1.0 * votorist.rating) as media, ristorante.ID as id_rist from (select ristorante.* from ristorante, luogo where ristorante.id_luogo = luogo.id and luogo.city = ?) as ristorante Left Join votorist on (ristorante.ID = votorist.ID_RIST) group by (ristorante.ID) order by media desc nulls last");
            stm.setString(1, luogo.getCity());
            rs = stm.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id_rist") == getId()) {
                    break;
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
     *
     * @param giorno giorno a cui è riferito l'orario, 1 = Lunedì, 2 = Martedì,
     * .... , 7 = Domenica
     * @param inizio Time dell'inizio dell'orario di apertura
     * @param fine Time della fine dell'orario di apertura
     * @return
     */
    public boolean addTimesToRistorante(int giorno, Time inizio, Time fine) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("select * from days where id_rist = ? AND giorno = ?");
            stm.setInt(1, getId());
            stm.setInt(2, giorno);
            rs = stm.executeQuery();
            if (rs.next()) {
                Days d = new Days(rs.getInt("id"), rs.getInt("giorno"), this, manager);
                d.addTimes(inizio, fine);
            } else if (addDays(giorno)) {
                stm = manager.con.prepareStatement("select * from days where id_rist = ? AND giorno = ?");
                stm.setInt(1, getId());
                stm.setInt(2, giorno);
                rs = stm.executeQuery();
                if (rs.next()) {
                    Days d = new Days(rs.getInt("id"), rs.getInt("giorno"), this, manager);
                    d.addTimes(inizio, fine);
                }
            }
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

    public boolean addDays(int giorno) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("insert into days (giorno, id_rist) values (?,?)");
            stm.setInt(1, giorno);
            stm.setInt(2, getId());
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

    public boolean removeTimes(int id_times) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("delete from times where id = ?");
            stm.setInt(1, id_times);
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
     * Per ottenere tutti gli orari di questo ristorante
     *
     * @return tutti gli orari del ristorante
     */
    public ArrayList<Days> getDays() {
        PreparedStatement stm = null;
        ArrayList<Days> res = new ArrayList<>();
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("SELECT * FROM Days WHERE id_rist = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();

            while (rs.next()) {
                res.add(new Days(rs.getInt("id"), rs.getInt("giorno"), this, manager));
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
        Comparator c = (Comparator<Days>) new Comparator<Days>() {
            @Override
            public int compare(Days o1, Days o2) {
                return o1.getGiorno() > o2.getGiorno() ? 1 : -1;
            }
        };
        res.sort(c);
        return res;
    }

    /**
     * Per ottenere tutte le recensioni lasciate dagli utenti a questo
     * ristorante
     *
     * @return Lista di Recensioni
     */
    public ArrayList<Recensione> getRecensioni() {
        PreparedStatement stm = null;
        ArrayList<Recensione> res = new ArrayList<>();
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("SELECT * FROM RECENSIONE WHERE id_rist = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();

            while (rs.next()) {
                res.add(new Recensione(rs.getInt("id"), rs.getString("titolo"), rs.getString("testo"), rs.getDate("data"), rs.getString("commento"), rs.getString("fotopath"), this, manager.getUtente(rs.getInt("id_utente")), manager));
            }
            Comparator c = (Comparator<Recensione>) (Recensione o1, Recensione o2) -> {
                if (o1.getData().after(o2.getData())) {
                    return -1;
                } else if (o1.getData().equals(o2.getData())) {
                    return 0;
                } else {
                    return 1;
                }
            };
            res.sort(c);
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
     * Questa funzione permette di aggiungere una recensione a questo ristorante
     *
     * @param titolo titolo della recensione
     * @param testo testo o corpo della recensione
     * @param utente utente che scrive la recensione
     * @return l'oggetto recensione appena creato
     */
    public Recensione addRecensione(String titolo, String testo, Utente utente) {
        if (utente == null) {
            return null;
        }
        Recensione res = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Date current = Date.valueOf(LocalDate.now());
        try {
            stm = manager.con.prepareStatement("INSERT INTO RECENSIONE (titolo,testo,data,id_utente,id_rist) VALUES (?,?,?,?,?)");
            stm.setString(1, titolo);
            stm.setString(2, testo);
            stm.setDate(3, current);
            stm.setInt(4, utente.getId());
            stm.setInt(5, getId());
            stm.executeUpdate();

            stm = manager.con.prepareStatement("SELECT * FROM RECENSIONE where id_utente = ? AND id_rist = ? ");
            stm.setInt(1, utente.getId());
            stm.setInt(2, getId());
            rs = stm.executeQuery();
            if (rs.next()) {
                res = new Recensione(rs.getInt("id"), rs.getString("titolo"), rs.getString("testo"), rs.getDate("data"), rs.getString("commento"), rs.getString("fotopath"), this, utente, manager);
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
     * Funzione per l'aggiunta di una foto a questo ristorante
     *
     * @param path path della foto da aggiungere
     * @param descr piccola descrizione della foto
     * @param utente utente che aggiunge la foto
     * @return true se l'aggiunta ha avuto successo, false altrimenti
     */
    public Foto addFoto(String path, String descr, Utente utente) {
        if (utente == null) {
            return null;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        Foto res = null;
        try {
            stm = manager.con.prepareStatement("INSERT INTO FOTO (fotopath, descr, data, id_rist, id_utente) VALUES (?,?,?,?,?)");
            stm.setString(1, path);
            stm.setString(2, descr);
            Date current = Date.valueOf(LocalDate.now());
            stm.setDate(3, current);
            stm.setInt(4, id);
            stm.setInt(5, utente.getId());
            stm.executeUpdate();

            stm = manager.con.prepareStatement("select * from foto where fotopath = ? and descr = ?");
            stm.setString(1, path);
            stm.setString(2, descr);
            rs = stm.executeQuery();
            if (rs.next()) { //int id, String fotopath, String descr, Date data, Utente utente, Ristorante ristorante, DBManager manager
                res = new Foto(rs.getInt("id"), rs.getString("fotopath"), rs.getString("descr"), rs.getDate("data"), manager.getUtente(rs.getInt("id_utente")), this, manager);
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
     * Funzione per rimuovere una foto da questo ristorante
     *
     * @param foto foto da rimuovere
     * @return true se la rimozione ha avuto successo, false altrimenti
     */
    public boolean removeFoto(Foto foto) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = manager.con.prepareStatement("DELETE FROM FOTO WHERE id = ?");
            stm.setInt(1, foto.getId());
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
     * Funione per ottenere una lista di tutte le foto che sono state aggiunte a
     * questo ristorante
     *
     * @return ArrayList di foto di questo ristorante
     */
    public ArrayList<Foto> getFoto() {
        ArrayList<Foto> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = manager.con.prepareStatement("select * from foto where id_rist = ?");
            stm.setInt(1, getId());
            rs = stm.executeQuery();
            while (rs.next()) { //int id, String fotopath, String descr, Date data
                res.add(new Foto(rs.getInt("id"), rs.getString("fotopath"), rs.getString("descr"), rs.getDate("data"), manager.getUtente(rs.getInt("id_utente")), this, manager));
            }
            Comparator c = (Comparator<Foto>) (Foto o1, Foto o2) -> {
                if (o1.getData().after(o2.getData())) {
                    return -1;
                } else if (o1.getData().equals(o2.getData())) {
                    return 0;
                } else {
                    return 1;
                }
            };
            res.sort(c);
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
        if (res.isEmpty()) { //int id, String fotopath, String descr, Date data, Utente utente, Ristorante ristorante, DBManager manager
            res.add(new Foto(-1, "/defaultRist.png", "Default Image", new Date(System.currentTimeMillis()), null, this, manager));
        }
        return res;
    }

    /**
     * Crea un immagine QR con le seguenti informazioni: - Nome - Indirizzo -
     * Orari di apertura
     *
     * @return il path per accedere all'immagine creata
     */
    public String creaQR() {

        String name = "/" + this.getNome().replace(' ', '-').replace('/','-') + ".jpg";
        String savePath = manager.completePath + manager.fotoFolder + name;

        ArrayList<Days> days = getDays();

        String forQR = "Nome ristorante: " + nome.trim();

        if (luogo != null) {
            forQR += "\n" + "Indirizzo: " + luogo.getAddress() + "\n";
        }

        if (days != null) {
            for (Days o : days) {
                for (Times t : o.getTimes()) {
                    forQR = forQR + t.toString() + '\n';
                }
            }
        }
        System.out.println("RistQrpath: " + savePath);
        ByteArrayOutputStream out = QRCode.from(forQR).to(ImageType.JPG).stream();
        FileOutputStream fout;
        try {

            fout = new FileOutputStream(new File(savePath));
            fout.write(out.toByteArray());
            fout.flush();
            fout.close();

        } catch (IOException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            name = "/FotoServlet" + "/default.jpg";
        }
        return "/FotoServlet" + name;

    }

    /**
     * Aggiunge un voto a questo ristorante
     *
     * @param user utente che aggiunge il voto
     * @param rating voto da 0 a 5 compresi
     * @return true se il voto è stato aggiunto con successo, false altrimenti
     */
    public boolean addVoto(Utente user, int rating) {
        if (user == null) {
            return false;
        }
        PreparedStatement stm = null;
        Date current = Date.valueOf(LocalDate.now());
        boolean res = false;
        if (user.justVotatoOggi(this)) {
            res = false;
        } else {
            try {
                stm = manager.con.prepareStatement("INSERT INTO votorist (id_utente, id_rist, data, rating) VALUES (?,?,?,?)");
                stm.setInt(1, user.getId());
                stm.setInt(2, getId());
                stm.setDate(3, current);
                stm.setInt(4, rating);
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
        }
        return res;
    }

    //cerca i 20 ristoranti più vicini
    public ArrayList<Ristorante> getVicini() {
        if (luogo != null) {
            return manager.searchVicini(luogo.getLat(), luogo.getLng(), 20);
        } else {
            return new ArrayList<>();
        }
    }

    //da lavorare
    public boolean nowOpen() {
        return true;
    }

    @Override
    public Ristorante clone() throws CloneNotSupportedException {
        super.clone();
        Ristorante clone = new Ristorante(id, nome, descr, linksito, fascia, cucina, utente, visite, luogo, manager);
        return clone;
    }
}
