package Servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import DataBase.DBManager;
import DataBase.Language;
import DataBase.Recensione;
import DataBase.Ristorante;
import DataBase.Utente;
import Mail.EmailSessionBean;
import Support.Encoding;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class InserisciRecensioniServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");

        MultipartRequest multi = new MultipartRequest(request, manager.completePath + manager.fotoFolder, 10 * 1024 * 1024, "ISO-8859-1", new FileRenamePolicy() {
            @Override
            public File rename(File file) {
                String filename = file.getName();
                int dot = filename.lastIndexOf(".");
                String ext = filename.substring(dot);
                String name = filename.substring(dot, filename.length());
                String newname;
                try {
                    newname = (name + (new Date()).toString() + EmailSessionBean.encrypt(file.getName()) + Encoding.getNewCode()).replace(".", "").replace(" ", "_").replace(":", "-") + ext;
                } catch (UnsupportedEncodingException ex) {
                    newname = (name + (new Date()).toString() + Encoding.getNewCode()).replace(".", "").replace(" ", "_").replace(":", "-") + ext;
                }
                File f = new File(file.getParent(), newname);
                if (createNewFile(f)) {
                    session.setAttribute("newName", newname);
                    return f;
                } else {
                    session.setAttribute("newName", null);
                    return null;
                }
            }

            private boolean createNewFile(File f) {
                try {
                    return f.createNewFile();
                } catch (IOException ex) {
                    return false;
                }
            }
        });

        Enumeration files = multi.getFileNames();
        String name = null;
        while (files.hasMoreElements()) {
            name = (String) files.nextElement();
        }

        String titolo = multi.getParameter("titolo");
        String recensione = multi.getParameter("rec");
        recensione = Encoding.Parsifica(recensione);
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());

        boolean tornaIndietro = false;

        if (titolo.length() < 3) {
            request.setAttribute("recMessage1", labels.getString("tit.short"));
            tornaIndietro = true;
        }

        if (recensione.length() < 10) {
            request.setAttribute("recMessage2", labels.getString("rec.short"));
            tornaIndietro = true;
        }

        if (tornaIndietro) {
            request.getRequestDispatcher("/private/scriviRecensione.jsp").forward(request, response);
        } else {
            String fotoPath;
            if (session.getAttribute("newName") == null) {
                fotoPath = "/rec_default.png";
            } else {
                fotoPath = "/" + session.getAttribute("newName");
                session.removeAttribute("newName");
            }

            Recensione rec = ristorante.addRecensione(titolo, recensione, utente);
            if (rec != null) {
                rec.addFoto(fotoPath);
                manager.newNotNuovaRecensione(rec);
                request.getRequestDispatcher("/info.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/private/scriviRecensione.jsp").forward(request, response);
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(InserisciRecensioniServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(InserisciRecensioniServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
