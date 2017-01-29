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
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.oreilly.servlet.MultipartRequest;

import com.oreilly.servlet.multipart.FileRenamePolicy;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;

/**
 *
 * @author bazza
 */
@MultipartConfig
public class ModificaProfiloServlet extends HttpServlet {

    private DBManager manager;
    private String dirName;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");

        // read the uploadDir from the servlet parameters
        dirName = config.getInitParameter("uploadDir");
        if (dirName == null) {
            throw new ServletException("Please supply uploadDir parameter");
        }
    }

    @SuppressWarnings("empty-statement")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        response.setContentType("text/plain"); //tipo di file di upload
        Utente utente = (Utente) session.getAttribute("utente");
        RequestDispatcher rd = request.getRequestDispatcher("/private/ConfigurazioneProfilo");
        if (utente != null) {

            MultipartRequest multi = new MultipartRequest(request, manager.completePath + "/web" + dirName, 10 * 1024 * 1024, "ISO-8859-1", new FileRenamePolicy() {
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
            String fileName = null;
            while (files.hasMoreElements()) {
                fileName = (String) files.nextElement();
            }

            ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());
            
            String newNome = null;
            String newCognome = null;
            String newMail = null;
            String oldPass = null;
            String newPass = null;
            String newAvPath = null;

            boolean tornaIndietro = false;

            if (!multi.getParameter("nome").equals("")) {
                if (multi.getParameter("nome").length() < 2) {
                    request.setAttribute("message", labels.getString("name.corto"));
                    tornaIndietro = true;
                } else {
                    newNome = multi.getParameter("nome");
                }
            }

            if (!multi.getParameter("cognome").equals("")) {
                if (multi.getParameter("cognome").length() < 3) {
                    request.setAttribute("message", labels.getString("cognome.corto"));
                    tornaIndietro = true;
                } else {
                    newCognome = multi.getParameter("cognome");
                }
            }

            if (!multi.getParameter("mail").equals("")) {
                if (multi.getParameter("mail").length() < 8 || !multi.getParameter("mail").contains("@")) {
                    request.setAttribute("message", labels.getString("invalid.mail"));
                    tornaIndietro = true;
                } else {
                    newMail = multi.getParameter("mail");
                }
            }

            if (!multi.getParameter("passOld").equals("") || !multi.getParameter("pass1").equals("") || !multi.getParameter("pass2").equals("")) {
                if (!multi.getParameter("passOld").equals(utente.getPassword())) {
                    request.setAttribute("message", labels.getString("old.psw.inv"));
                    tornaIndietro = true;
                } else if (!(multi.getParameter("pass1").equals(multi.getParameter("pass2")))) {
                    request.setAttribute("message", labels.getString("psw.corr"));
                    tornaIndietro = true;
                } else if (multi.getParameter("pass1").length() < 8) {
                    request.setAttribute("message", labels.getString("min.length"));
                    tornaIndietro = true;
                } else {
                    oldPass = multi.getParameter("passOld");
                    newPass = multi.getParameter("pass1");
                }
            }
            
            if (!(session.getAttribute("newName") == null)) {
                newAvPath = dirName + "/" + session.getAttribute("newName");
                session.removeAttribute("newName");
            }

            if (tornaIndietro) {
                rd = request.getRequestDispatcher("/private/modifica.jsp");
            } else {
                utente.updateNome(newNome);
                utente.updateCognome(newCognome);
                utente.updateEmail(newMail);
                utente.updateAvpath(newAvPath);
                utente.updatePassword(oldPass, newPass);
                session.setAttribute("utente", manager.authenticate(utente.getEmail(), utente.getPassword()));
            }
        }
        rd.forward(request, response);
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
            Logger.getLogger(ModificaProfiloServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ModificaProfiloServlet.class.getName()).log(Level.SEVERE, null, ex);
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
