/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Foto;
import DataBase.Ristorante;
import DataBase.Utente;
import Mail.EmailSessionBean;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AddFotoServlet extends HttpServlet {

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        response.setContentType("text/plain");
        Utente utente = (Utente) session.getAttribute("utente");
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");

        MultipartRequest multi = new MultipartRequest(request, manager.completePath + "/web" + dirName, 10 * 1024 * 1024, "ISO-8859-1", new FileRenamePolicy() {
            @Override
            public File rename(File file) {
                try {
                    return new File(file.getName() + (new Date()).toString() + EmailSessionBean.encrypt(file.getName()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AddFotoServlet.class.getName()).log(Level.SEVERE, null, ex);
                    return file;
                }
            }
        });

        Enumeration files = multi.getFileNames();
        String name = null;
        while (files.hasMoreElements()) {
            name = (String) files.nextElement();
        }
        RequestDispatcher rd;
        
        if (multi.getFilesystemName(name) == null) {
            request.setAttribute("errMessage", "Selezione non valida, riprova");
            rd = request.getRequestDispatcher("/private/choose.jsp");
        } else {
            String newAvPath = dirName + "/" + multi.getFilesystemName(name);
            Foto foto = ristorante.addFoto(newAvPath, multi.getParameter("descr"), utente);
            System.out.println(foto);
            if (foto != null) {
                manager.newNotNuovaFoto(foto);
                rd = request.getRequestDispatcher("/ConfigurazioneRistorante?id_rist=" + ristorante.getId());

            } else {
                request.setAttribute("errMessage", "Errore nel caricamento, riprova");
                rd = request.getRequestDispatcher("/private/choose.jsp");
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
            Logger.getLogger(AddFotoServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AddFotoServlet.class.getName()).log(Level.SEVERE, null, ex);
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
