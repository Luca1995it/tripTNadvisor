/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Utente;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Enumeration;
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException, SQLException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        response.setContentType("text/plain"); //tipo di file di upload
        Utente utente = (Utente) session.getAttribute("utente");

        MultipartRequest multi = new MultipartRequest(request, manager.completePath + "/web" + dirName, 10 * 1024 * 1024, "ISO-8859-1", new DefaultFileRenamePolicy());
        Enumeration files = multi.getFileNames();
        String name = null;
        while (files.hasMoreElements()) {
            name = (String) files.nextElement();
        }

        String nome = multi.getParameter("nome");
        String descr = multi.getParameter("desc");
        String linkSito = multi.getParameter("linkSito");
        String [] spec = multi.getParameterValues("spec");

        String addr = multi.getParameter("addr");
        String fascia = multi.getParameter("fascia");
        String fotoPath = dirName + "/" + multi.getFilesystemName(name);
        String fotoDescr = multi.getParameter("fotoDescr");

        boolean tornaIndietro = false;

        if (nome == null || descr == null || linkSito == null || addr == null || fascia == null || fotoPath == null || fotoDescr == null || spec == null) {
            request.setAttribute("errMessageAdd", "errore interno, riprovare");
            request.getRequestDispatcher("/privateRistoratore/ConfigurazioneAddRistorante").forward(request, response);
        } else {

            if (nome.equals("") || descr.equals("")) {
                tornaIndietro = true;
                request.setAttribute("error", "devi riempire almeno nome e descrizione");
            }

            if (fotoPath.equals("") || fotoDescr.equals("")) {
                tornaIndietro = true;
                request.setAttribute("errorFoto", "Devi riempire anche i campi della prima fotografia");
            }

            if(!manager.okLuogo(addr)){
                tornaIndietro = true;
                request.setAttribute("addrError", "Inserisci un indirizzo del ristorante valido");
            }
            
            if (manager.esisteNomeRistorante(nome)) {
                request.setAttribute("nomeError", "Nome già in uso");
                tornaIndietro = true;
            }
            
            if(spec.length == 0){
                request.setAttribute("specError", "Seleziona almeno una specialita");
                tornaIndietro = true;
            }

            if (tornaIndietro) {
                request.getRequestDispatcher("/private/ConfigurazioneAddRistorante").forward(request, response);
            } else {
                utente.addRistorante(nome, descr, linkSito, fascia, spec, addr, fotoPath, fotoDescr);
                request.setAttribute("message", "Ristorante aggiunto correttamente");
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
