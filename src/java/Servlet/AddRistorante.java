/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Utente;
import Mail.EmailSessionBean;
import Support.Encoding;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
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

/**
 *
 * @author lucadiliello
 */
public class AddRistorante extends HttpServlet {

    private DBManager manager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        response.setContentType("text/plain"); //tipo di file di upload
        Utente utente = (Utente) session.getAttribute("utente");

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

        String nome = multi.getParameter("nome");
        String descr = multi.getParameter("desc");
        String linkSito = multi.getParameter("linkSito");
        String[] spec = multi.getParameterValues("spec");

        String addr = multi.getParameter("addr");
        String fascia = multi.getParameter("fascia");
        String fotoPath = (String) session.getAttribute("newName");
        session.removeAttribute("newName");
        String fotoDescr = multi.getParameter("fotoDescr");

        boolean tornaIndietro = false;
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());


        if (nome == null || descr == null || linkSito == null || addr == null || fascia == null || fotoDescr == null) {
            request.setAttribute("errMessageAdd", labels.getString("error.internal"));
            request.getRequestDispatcher("/private/ConfigurazioneAddRistorante").forward(request, response);
        } else {

            if (nome.length() < 3) {
                tornaIndietro = true;
                request.setAttribute("nomeError", labels.getString("missing.name"));
            }
            
            if (descr.length() < 10){
                tornaIndietro = true;
                request.setAttribute("errorDescr", labels.getString("missing.descr"));
            }

            if (fotoPath == null || fotoPath.equals("/") || fotoDescr.equals("")) {
                tornaIndietro = true;
                request.setAttribute("errorFoto", labels.getString("missing.foto"));
            }

            if (!manager.okLuogo(addr)) {
                tornaIndietro = true;
                request.setAttribute("addrError", labels.getString("missing.addr"));
            }

            if (manager.esisteNomeRistorante(nome)) {
                request.setAttribute("nomeError", labels.getString("nome.uso"));
                tornaIndietro = true;
            }

            if (spec == null || spec.length == 0) {
                request.setAttribute("specError", labels.getString("missing.spec"));
                tornaIndietro = true;
            }
            
            if (tornaIndietro) {
                request.getRequestDispatcher("/private/ConfigurazioneAddRistorante").forward(request, response);
            } else {
                utente.addRistorante(nome, descr, linkSito, fascia, spec, addr, "/" + fotoPath, fotoDescr);
                session.setAttribute("utente", manager.getUtente(utente.getId()));
                request.setAttribute("message", labels.getString("restaurant.added"));
                request.getRequestDispatcher("/HomeServlet").forward(request, response);
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

        } catch (ParseException ex) {
            Logger.getLogger(AddRistorante.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            Logger.getLogger(AddRistorante.class
                    .getName()).log(Level.SEVERE, null, ex);
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

        } catch (ParseException ex) {
            Logger.getLogger(AddRistorante.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            Logger.getLogger(AddRistorante.class
                    .getName()).log(Level.SEVERE, null, ex);
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
