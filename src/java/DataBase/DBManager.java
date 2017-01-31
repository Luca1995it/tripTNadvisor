package DataBase;

import Support.InvalidAddresException;
import Support.MapsParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.json.JSONObject;

public final class DBManager implements Serializable {

    //transient == non viene serializzato
    public transient Connection con;
    protected final String googleKey;
    public final String completePath;
    public final int port;
    public String fotoFolder;

    public DBManager(int port, String dburl, String user, String password, String completePath, String fotoFolder) {
        this.googleKey = "AIzaSyA7spDhgAtLeyh6b0F6MQI2I5fldqrR6oM";
        this.completePath = completePath;
        this.fotoFolder = fotoFolder;
        this.port = port;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver", true, getClass().getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
        try {
            con = DriverManager.getConnection(dburl, user, password);
            updateAutocomplete();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Invocato per spegnere la connessione al DB
     */
    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).info(ex.getMessage());
        }
    }

    /////////////////// METODI GENERALI //////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    public InetAddress getCurrentIp() {
        InetAddress ip;
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    /**
     * Metodo per verificare l'autenticazione di un utente
     *
     * @param email email dell'utente a cui fare il login
     * @param password password dell'utente
     * @return un oggetto Utente se la email e la password corrispondono ad un
     * Utente nel DB, null altrimenti
     */
    public Utente authenticate(String email, String password) {
        PreparedStatement stm = null;
        ResultSet rs;
        Utente res = null;
        try {
            stm = con.prepareStatement("SELECT id FROM UTENTE WHERE email = ? AND password = ?");
            stm.setString(1, email);
            stm.setString(2, password);

            rs = stm.executeQuery();
            if (rs.next()) {
                res = getUtente(rs.getInt("id"));
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
     * Per ottenere le ultime (fino a) 5 recensioni lasciate sul portale
     *
     * @return un ArrayList di 5 recensioni
     */
    public ArrayList<Recensione> getUltimeRecensioni() {
        ArrayList<Recensione> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            //int id, String titolo, String testo, Date data, String commento, String fotoPath, DBManager manager
            stm = con.prepareStatement("select * from recensione order by data desc nulls last { limit 5 }");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new Recensione(rs.getInt("id"), rs.getString("titolo"), rs.getString("testo"), rs.getDate("data"), rs.getString("commento"), rs.getString("fotopath"), getRistorante(rs.getInt("id_rist")), getUtente(rs.getInt("id_utente")), this));
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
     * Per ottenere fino a 5 ristoranti con migliori voti (calcolati come media
     * dei voti lasciati dagli utenti ad ogni ristorante)
     *
     * @return un ArrayList di 5 Ristoranti
     */
    public ArrayList<Ristorante> getRistorantiPiuVotati() {
        ArrayList<Ristorante> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            //int id, String titolo, String testo, Date data, String commento, String fotoPath, DBManager manager
            stm = con.prepareStatement("select * from( select ristorante.ID, avg(rating) as media from votorist, ristorante where ristorante.ID = votorist.ID_RIST group by ristorante.ID) as res, ristorante as ristorante where res.id = ristorante.ID order by res.media desc nulls last { limit 5 }");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linkSito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this));
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
     * Per ottenere fino a 5 ristoranti che hanno ricevuto più visite
     *
     * @return un ArrayList di 5 Ristoranti
     */
    public ArrayList<Ristorante> getRistorantiPiuVisitati() {
        ArrayList<Ristorante> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            //int id, String titolo, String testo, Date data, String commento, String fotoPath, DBManager manager
            stm = con.prepareStatement("SELECT * FROM ristorante order by ristorante.VISITE desc nulls last { limit 5 } ");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linkSito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this));
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
     * Per controllare se un utente con quella mail è già registrato
     *
     * @param email mail da controllare
     * @return true se esiste già un utente registrato con quella mail, false
     * altrimenti
     */
    public boolean esisteMail(String email) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("select * from Utente where email = ?");
            stm.setString(1, email);
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
     *
     * @param utente
     * @param key
     * @return
     */
    public boolean addKey(Utente utente, String key) {
        PreparedStatement stm = null;
        Boolean res = false;
        try {
            stm = con.prepareStatement("insert into Validation (id_utente,chiave) values (?,?)");
            stm.setInt(1, utente.getId());
            stm.setString(2, key);
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
     * Per controllare se esiste già un Ristorante con quel nome registrato nel
     * portale
     *
     * @param nome nome del ristorante da controllare
     * @return true se esiste già nel portale un ristorante con quel nome, false
     * altrimenti
     */
    public boolean esisteNomeRistorante(String nome) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("select * from Ristorante where nome = ?");
            stm.setString(1, nome);
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
     * Aggiunte un nuovo utente al portale
     *
     * @param nome nome del nuovo utente
     * @param cognome cognome del nuovo utente
     * @param email email del nuovo utente
     * @param password password del nuovo utente
     * @param privacy
     * @return l'oggetto Utente per la sessione
     */
    public Utente addRegistrato(String nome, String cognome, String email, String password, boolean privacy) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        Utente utente = null;
        try {
            stm = con.prepareStatement("INSERT INTO UTENTE (nome,cognome,email,password,accettato,avpath) VALUES (?,?,?,?,?,?)");
            stm.setString(1, nome);
            stm.setString(2, cognome);
            stm.setString(3, email);
            stm.setString(4, password);
            stm.setBoolean(5, privacy);
            stm.setString(6, "/default.jpg");
            stm.executeUpdate();

            stm = con.prepareStatement("select * from utente where email = ?");
            stm.setString(1, email);
            rs = stm.executeQuery();
            if (rs.next()) {
                utente = (getUtente(rs.getInt("id")));
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
        return utente;
    }

    /**
     * Questa funzione calcola la classifica istantanea degli utenti. Il
     * punteggio di ogni utente è calcolato come media dei voti che sono stati
     * dati alle sue recensioni
     *
     * @return un ArrayList di tutti gli utentu del portale, ordinati per
     * Classifica
     */
    public ArrayList<Utente> getClassificaUtenti() {
        ArrayList<Utente> res = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = con.prepareStatement("select utente.id as idU from (select avg(voti.rate) as mid, recensione.ID_UTENTE as "
                    + "id from (select recensione.id as id, avg(rating) as rate from recensione left join votorec on "
                    + "recensione.id = votorec.ID_REC group by recensione.id) as voti, recensione where voti.id = recensione.ID "
                    + "group by recensione.ID_UTENTE) as res right join (select * from utente where amministratore = false) as utente"
                    + " on res.id = utente.ID order by mid desc nulls last");
            rs = stm.executeQuery();

            while (rs.next()) {
                Utente u = getUtente(rs.getInt("idU"));
                res.add(u);
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

    public ArrayList<String> getSpecialita() {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<String> res = new ArrayList<>();
        try {
            stm = con.prepareStatement("select * from specialita");
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("nome"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
     * Controlla che un indirizzo venga riconosciuto correttamente da Google
     * Maps
     *
     * @param address indirizzo da controllare
     * @return true se l'indirizzo può essere elaborato e trasformato in
     * coordinate geografiche da google maps, false altrimenti
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }

    public boolean okLuogo(String address) {
        String req = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(' ', '+') + "&key=" + googleKey;
        try {
            JSONObject json = readJsonFromUrl(req);
            return json.get("status").equals("OK");
        } catch (IOException | JSONException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Funzione di ricerca di base, implementata nella barra di ricerca
     * secondaria e non specifica (quella in alto sulla nav-bar). Esegue la
     * ricerca del campo research tra tutti i nomi dei ristoranti e tra tutti
     * gli indirizzi dei ristoranti. E' possibile quindi utilizzarla per cercare
     * ristoranti per nome p per località
     *
     * @param research campo di ricerca
     * @param place
     * @param tipo
     * @param spec
     * @param lat
     * @param lng
     * @param labels
     * @return un ArrayList dei Ristoranti trovati
     */
    public ArrayList<Ristorante> search(String research, String place, String tipo, String spec, String lat, String lng, ResourceBundle labels) {

        ArrayList<Ristorante> original = new ArrayList<>();
        ArrayList<Ristorante> res;
        ArrayList<Ristorante> fin;
        System.out.println("-----------------------------");
        System.out.println("Search on: " + research + ", " + place + ", " + tipo + ", " + spec + ", " + lat + ", " + lng);

        PreparedStatement stm = null;
        ResultSet rs = null;

        if ((lat != null) && (lng != null) && !lat.equals("") && !lng.equals("")) {
            System.out.println("Entro primo if");
            for (int k = 4; k < 10; k++) {
                System.out.println("Entro nel for con lat: "+lat+" e lng: "+lng);
                original = searchVicini(Double.parseDouble(lat), Double.parseDouble(lng), k * 10);
                if (spec != null && !spec.equals("") && !spec.equals("all")) {
                    for (Iterator i = original.iterator(); i.hasNext();) {
                        Ristorante r = (Ristorante) i.next();
                        System.out.println("Simil? " + spec + " " + r.getCucina() + ": " + similString(r.getCucina(), spec, 1));

                        if (!similString(r.getCucina(), spec, 1)) {
                            i.remove();
                        }
                    }
                }
                if (original.size() > 30) {
                    break;
                }
            }

        } else {
            original = new ArrayList<>();
            if (place != null && !place.equals("") && okLuogo(place)) {
                for (int k = 4; k < 10; k++) {
                    try {
                        MapsParser mp = new MapsParser(place, googleKey);
                        System.out.println("MP: " + mp.getCity() + " " + mp.getState());
                        original = searchVicini(mp.getLat(), mp.getLat(), k * 10);

                    } catch (InvalidAddresException ex) {
                        try {
                            stm = con.prepareStatement("SELECT * FROM RISTORANTE");
                            rs = stm.executeQuery();
                            while (rs.next()) {
                                original.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linkSito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this));
                            }
                        } catch (SQLException ex1) {
                            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            if (stm != null) {
                                try {
                                    stm.close();
                                } catch (SQLException ex1) {
                                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (rs != null) {
                                try {
                                    rs.close();
                                } catch (SQLException ex1) {
                                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                    }
                    if (spec != null && !spec.equals("") && !spec.equals("all")) {
                        for (Iterator i = original.iterator(); i.hasNext();) {

                            Ristorante r = (Ristorante) i.next();
                            System.out.println("Simil? " + spec + " " + r.getCucina() + ": " + similString(r.getCucina(), spec, 1));
                            if (!similString(r.getCucina(), spec, 1)) {
                                i.remove();
                            }
                        }
                    }
                    if (original.size() > 30) {
                        break;
                    }
                }

            } else {
                try {
                    stm = con.prepareStatement("SELECT * FROM RISTORANTE");
                    rs = stm.executeQuery();
                    while (rs.next()) {
                        original.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linkSito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this));
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
                if (spec != null && !spec.equals("") && !spec.equals("all")) {
                    for (Iterator i = original.iterator(); i.hasNext();) {
                        Ristorante r = (Ristorante) i.next();
                        System.out.println("Simil? " + spec + " " + r.getCucina() + ": " + similString(r.getCucina(), spec, 1));
                        if (!similString(r.getCucina(), spec, 1)) {
                            i.remove();
                        }
                    }
                }
            }
        }

        String name;
        ArrayList<String> cucina;
        res = new ArrayList<>();

        if (research != null && !research.equals("")) {
            for (int k = 0; k < 10; k++) {

                for (Ristorante r : original) {

                    cucina = parseCucina(r.getCucina(), labels);
                    if (spec.toLowerCase().equals("all") || similString(cucina, spec, k)) {
                        name = r.getNome().toLowerCase();
                        cucina = parseCucina(r.getCucina(), labels);
                        System.out.println("Confronto: " + cucina + " " + research + " " + name + " res: " + (similString(name, research, k) || similString(cucina, research, k)));
                        if (similString(name, research, k) || similString(cucina, research, k)) {
                            res.add(r);
                        }
                    }
                }
                for (Ristorante r : res) {
                    original.remove(r);
                }
                if (res.size() > 10) {
                    break;
                }
            }
        } else {
            res = original;
        }

        if (!tipo.equals("all")) {

            double midVisite = 0;

            for (Ristorante r : res) {
                midVisite += r.getVisite();
            }
            midVisite /= res.size();

            fin = new ArrayList<>();

            for (int k = 0; k < 10; k++) {
                for (Ristorante r : res) {
                    if (r.getVisite() > midVisite) {
                        fin.add(r);
                    }
                }

                for (Ristorante r : fin) {
                    res.remove(r);
                }
                if (fin.size() > res.size()) {
                    break;
                }
                midVisite /= 1.2;
            }

            return fin;

        } else {
            return res;
        }

    }

    ArrayList<String> parseCucina(ArrayList<String> a, ResourceBundle labels) {
        ArrayList<String> res = new ArrayList<>();
        a.stream().map((l) -> {
            res.add(l);
            return l;
        }).forEach((l) -> {
            res.add(labels.getString(l));
        });
        return res;
    }

    public boolean similString(ArrayList<String> a, String b, int k) {
        if (a == null || b == null) {
            return false;
        }
        return a.stream().anyMatch((x) -> (similString(x, b, k)));
    }

    public boolean similString(String a, String b, int k) {
        if (a == null || b == null) {
            return false;
        } else {
            a = a.toLowerCase();
            b = b.toLowerCase();
            if (k > 3) {
                return (Levenshtein_distance(a, b) <= k) || a.contains(b.subSequence(0, b.length())) || b.contains(a.subSequence(0, a.length()));
            } else {
                return Levenshtein_distance(a, b) <= k;
            }
        }
    }

    int Levenshtein_distance(String x, String y) {
        int m = x.length();
        int n = y.length();

        int i, j;
        int distance;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        int[] tmp;

        for (i = 0; i <= n; i++) {
            prev[i] = i;
        }

        for (i = 1; i <= m; i++) {
            curr[0] = i;
            for (j = 1; j <= n; j++) {
                if (x.charAt(i - 1) != y.charAt(j - 1)) {
                    int k = minimum(curr[j - 1], prev[j - 1], prev[j]);
                    curr[j] = k + 1;
                } else {
                    curr[j] = prev[j - 1];
                }
            }

            tmp = prev;
            prev = curr;
            curr = tmp;

            curr = new int[n + 1];
        }

        distance = prev[n];

        return distance;
    }

    int minimum(int a, int b, int c) {
        int res = a;
        if (b < res) {
            res = b;
        }
        if (c < res) {
            res = c;
        }
        return res;
    }

    public String adjustLink(String link) {
        if (link == null || link.length() < 8) {
            return "";
        } else if (!link.substring(0, 7).equals("http://") || !link.substring(0, 8).equals("https://")) {
            return "http://" + link;
        } else {
            return link;
        }
    }

    /**
     * Per recupare l'oggetto utente con quell'id. Ad esso verrà effettuato un
     * downcast ad utente Registrato, Ristoratore, Amministratore secondo le
     * informazioni contenute nel DB
     *
     * @param mail
     * @return l'oggetto utente
     */
    public Utente getUtente(String mail) {
        PreparedStatement stm = null;
        Utente res = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            stm = con.prepareStatement("select id from Utente where email = ?");
            stm.setString(1, mail);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = getUtente(rs.getInt("id"));
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
            if (rs2 != null) {
                try {
                    rs2.close();

                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    /**
     * Attiva l'utente con il codice hash corrispondente nella tabella
     * Validation
     *
     * @param hash codice hash dell'utente da attivare
     * @return true se l'attivazione ha avuto successo, false altrimenti
     */
    public boolean activate(String hash) {
        PreparedStatement stm = null;
        boolean res = false;
        System.out.println("Ingresso activate");
        System.out.println("Hash: " + hash);
        try {
            stm = con.prepareStatement("update utente set attivato = ? where id = (select id_utente from validation where chiave = ?)");
            stm.setBoolean(1, true);
            stm.setString(2, hash);
            stm.executeUpdate();
            stm = con.prepareStatement("delete from validation where chiave = ?");
            stm.setString(1, hash);
            stm.executeUpdate();
            res = true;

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
        }
        return res;
    }

    /**
     * Assegna la proprietà di un ristorante ad un utente
     *
     * @param ristorante che verrà assegnato
     * @param utente che riceverà la proprietà del ristorante
     * @return
     */
    public boolean assegnaRistorante(Ristorante ristorante, Utente utente) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("update ristorante set id_utente = ? where id = ?");
            stm.setInt(1, utente.getId());
            stm.setInt(2, ristorante.getId());
            stm.executeUpdate();
            res = true;

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
        }
        return res;
    }

    public ArrayList<Ristorante> searchVicini(double lat, double lng, int k) {
        System.out.println("Entro in searchVicini con lat: "+lat+" e lng: "+lng);
        ArrayList<Ristorante> res = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement stm = null;
        try {
            System.out.println("Entro pre query");
            stm = con.prepareStatement("select * from ristorante, (SELECT ristorante.id , sqrt((?-l.LAT)*(?-l.lat) + (?-l.LNG)*(?-l.LNG)) as distance FROM RISTORANTE as ristorante, Luogo as l where ristorante.id_luogo = l.id) as res where res.id = ristorante.id order by distance asc nulls last { limit ? }");
            stm.setDouble(1, lat);
            stm.setDouble(2, lat);
            stm.setDouble(3, lng);
            stm.setDouble(4, lng);
            stm.setInt(5, k);
            rs = stm.executeQuery();
            while (rs.next()) {
                System.out.println("Ho trovato un ristorante");
                res.add(new Ristorante(rs.getInt("id"), rs.getString("nome"), rs.getString("descr"), rs.getString("linkSito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this));
                System.out.println("id: "+rs.getInt("id")+" nome: "+rs.getString("nome"));
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

    public boolean updateAutocomplete() {
        String path1 = "/web/autocompleteRist.txt";
        String path2 = "/web/autocompletePlace.txt";
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean res = false;
        try {

            File file1 = new File(this.completePath + path1);
            File file2 = new File(this.completePath + path2);
            String content1 = "";
            String content2 = "";

            stm = con.prepareStatement("select ristorante.nome as nome from ristorante");
            rs = stm.executeQuery();
            while (rs.next()) {
                content1 += rs.getString("nome") + ",";
            }

            stm = con.prepareStatement("select nome from specialita");

            Language lan = new Language();
            for (String a : lan.getLanguage()) {
                ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + a);
                rs = stm.executeQuery();
                while (rs.next()) {
                    content1 += labels.getString(rs.getString("nome")) + ",";
                }
            }

            stm = con.prepareStatement("select * from luogo");
            rs = stm.executeQuery();
            while (rs.next()) {
                content2 += rs.getString("street") + "," + rs.getString("city") + "," + rs.getString("area1") + "," + rs.getString("area2") + "," + rs.getString("state") + ",";
            }

            // if file doesn't exists, then create it
            FileOutputStream fop1 = new FileOutputStream(file1);
            // if file doesn't exists, then create it
            if (!file1.exists()) {
                file1.createNewFile();
            }
            // get the content in bytes
            byte[] contentInBytes1 = content1.getBytes();

            fop1.write(contentInBytes1);
            fop1.flush();

            // if file doesn't exists, then create it
            FileOutputStream fop2 = new FileOutputStream(file2);
            // if file doesn't exists, then create it
            if (!file2.exists()) {
                file2.createNewFile();
            }
            // get the content in bytes
            byte[] contentInBytes2 = content2.getBytes();

            fop2.write(contentInBytes2);
            fop2.flush();

            res = true;
        } catch (SQLException | IOException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
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

    ///////////////// METODI PER NOTIFICHE ///////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    /**
     * Crea una nuova notifica di tipo NuovaRecensione sul DB, per notificare un
     * utente ristoratore che un suo ristorante ha ricevuto una nuova foto
     *
     * @param foto la foto che è stata aggiunta al suo ristorante
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotNuovaFoto(Foto foto) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into nuovafoto(id_foto, data) values (?,?)");
            stm.setInt(1, foto.getId());
            stm.setDate(2, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
            res = true;
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
        }
        return res;
    }

    /**
     * Crea una nuova notifica di tipo NuovaRecensione sul DB, per notificare un
     * utente ristoratore che un suo ristorante ha ricevuto una nuova recensione
     *
     * @param recensione la nuova recensione al suo ristorante
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotNuovaRecensione(Recensione recensione) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into nuovarecensione(id_rec, data) values (?,?)");
            stm.setInt(1, recensione.getId());
            stm.setDate(2, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
            res = true;

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
        }
        return res;
    }

    /**
     * Crea una nuova notifica di tipo SegnalaFotoRecensione sul DB, verrà
     * estratta poi da un utente amministratore per essere verificata. Questa
     * notifica permette di far decidere ad un amministratore se la foto di
     * questa recensione è da togliere o meno
     *
     * @param recensione la recensione la cui foto vuole essere rimossa
     * dall'utente proprietario del ristorante sul quale è inserita la
     * recensione
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotSegnalaFotoRecensione(Recensione recensione) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into segnalafotorecensione(id_rec, data) values (?,?)");
            stm.setInt(1, recensione.getId());
            stm.setDate(2, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
            res = true;

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
        }
        return res;
    }

    /**
     * Crea una nuova notifica di tipo SegnalaFotoRistorante sul DB, verrà
     * estratta poi da un utente amministratore per essere verificata. Permette
     * ad un utente Ristoratore di segnalare una fotografia non consona alla
     * pagina del suo ristorante
     *
     * @param foto la fotografia del ristorante che si vuole segnalare
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotSegnalaFotoRistorante(Foto foto) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into segnalafotoristorante(id_foto, data) values (?,?)");
            stm.setInt(1, foto.getId());
            stm.setDate(2, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
            res = false;

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
        }
        return res;
    }

    /**
     * Crea una nuova notifica di tipo CommentoRecensione sul DB, verrà estratta
     * poi da un utente amministratore per essere verificata
     *
     * @param recensione la recensione a cui un utente ristoratore vuole
     * aggiungere il suo commento
     * @param commento il commento da aggiungere alla recensione
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotCommentoRecensione(Recensione recensione, String commento) {
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into RispostaRecensione(id_rec, commento, data) values (?,?,?)");
            stm.setInt(1, recensione.getId());
            stm.setString(2, commento);
            stm.setDate(3, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
            res = true;

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
        }
        return res;
    }

    /**
     * Crea una nuova notifica di tipo ReclamaRistorante sul DB per permettere
     * ad un amministratore di verificare se questo utente è il reale
     * proprietario del ristorante
     *
     * @param ristorante
     * @param utente l'utente che vuole reclamare quel ristorante
     * @return true se la notifica è stata registrata con successo sul DB, false
     * altriementi
     */
    public boolean newNotReclamaRistorante(Ristorante ristorante, Utente utente) {
        if (utente == null) {
            return false;
        }
        PreparedStatement stm = null;
        boolean res = false;
        try {
            stm = con.prepareStatement("insert into richiestaristorante(id_rist, id_utente, data) values (?,?,?)");
            stm.setInt(1, ristorante.getId());
            stm.setInt(2, utente.getId());
            stm.setDate(3, new Date(System.currentTimeMillis()));
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
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //
    //
    //
    //
    //
    ////////////// ESTRATTORI DA DB //////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Per recupare l'oggetto foto con quell'id
     *
     * @param id id della foto da ottenere
     * @return l'oggetto foto
     */
    public Foto getFoto(int id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        Foto res = null;
        try {
            stm = con.prepareStatement("select * from foto where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = new Foto(rs.getInt("id"), rs.getString("fotopath"), rs.getString("descr"), rs.getDate("data"), getUtente(rs.getInt("id_utente")), getRistorante(rs.getInt("id_rist")), this);

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

    /**
     * Per recupare l'oggetto utente con quell'id. Ad esso verrà effettuato un
     * downcast ad utente Registrato, Ristoratore, Amministratore secondo le
     * informazioni contenute nel DB
     *
     * @param id id dell'utente da ottenere
     * @return l'oggetto utente
     */
    public Utente getUtente(int id) {
        PreparedStatement stm = null;
        Utente res = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            stm = con.prepareStatement("select * from Utente where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("amministratore")) {
                    res = new Amministratore(rs.getInt("id"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("avpath"), this);
                } else {
                    stm = con.prepareStatement("SELECT COUNT(*) as res FROM RISTORANTE WHERE id_utente = ?");
                    stm.setInt(1, id);
                    rs2 = stm.executeQuery();
                    if (rs2.next()) {
                        //Ristoratore(int id, String nome, String cognome, String email, String avpath){
                        if (rs2.getInt("res") > 0) {
                            res = new Ristoratore(rs.getInt("id"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("avpath"), rs.getBoolean("attivato"), rs.getBoolean("accettato"), this);
                        } else {
                            res = new Registrato(rs.getInt("id"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getString("avpath"), rs.getBoolean("attivato"), rs.getBoolean("accettato"), this);

                        }
                    }
                }
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
            if (rs2 != null) {
                try {
                    rs2.close();

                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    /**
     * Per recupare l'oggetto recensione con quell'id
     *
     * @param id id della recensione da ottenere
     * @return l'oggetto recensione
     */
    public Recensione getRecensione(int id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        Recensione res = null;
        try {
            stm = con.prepareStatement("select * from Recensione where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = new Recensione(rs.getInt("id"), rs.getString("titolo"), rs.getString("testo"), rs.getDate("data"), rs.getString("commento"), rs.getString("fotopath"), getRistorante(rs.getInt("id_rist")), getUtente(rs.getInt("id_utente")), this);

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

    /**
     * Per recupare l'oggetto ristorante con quell'id
     *
     * @param id id del ristorante da ottenere
     * @return l'oggetto ristorante
     */
    public Ristorante getRistorante(int id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        Ristorante res = null;
        try {
            stm = con.prepareStatement("select * from Ristorante where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = new Ristorante(id, rs.getString("nome"), rs.getString("descr"), rs.getString("linksito"), rs.getString("fascia"), getCucina(rs.getInt("id")), getUtente(rs.getInt("id_utente")), rs.getInt("visite"), getLuogo(rs.getInt("id_luogo")), this);

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

    /**
     * Costruisce un oggetto di tipo Luogo sulla base dell'indirizzo fornito
     *
     * @param id
     * @return l'oggetto Luogo costruito
     */
    public Luogo getLuogo(int id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        Luogo res = null;
        try {
            stm = con.prepareStatement("select * from Luogo where id = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                res = new Luogo(rs.getInt("id"), rs.getDouble("lat"), rs.getDouble("lng"), rs.getString("street_number"), rs.getString("street"), rs.getString("city"), rs.getString("area1"), rs.getString("area2"), rs.getString("state"));

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

    public ArrayList<String> getCucina(int id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<String> res = new ArrayList<>();
        try {
            stm = con.prepareStatement("select nome from specrist, specialita where specrist.id_spec = specialita.id and specrist.id_rist = ?");
            stm.setInt(1, id);
            rs = stm.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("nome"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
}
