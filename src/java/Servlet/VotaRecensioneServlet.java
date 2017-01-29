/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Recensione;
import DataBase.Utente;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucadiliello
 */
public class VotaRecensioneServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        int id_rec = Integer.parseInt(request.getParameter("id_rec"));
        Recensione rec = manager.getRecensione(id_rec);
        Utente utente = (Utente) session.getAttribute("utente");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());

        if (!utente.justVotato(rec)) {
            if (rec.addVoto(utente, Integer.parseInt(request.getParameter("rating")))) {
                request.setAttribute("messageVotoRec", labels.getString("vote.reg"));
            } else {
                request.setAttribute("messageVotoRec", labels.getString("err.ins.vote"));
            }
        }
        request.setAttribute("messageVotoRec", labels.getString("non.rec"));

        request.getRequestDispatcher("/info.jsp").forward(request, response);
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
        processRequest(request, response);
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
        processRequest(request, response);
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
