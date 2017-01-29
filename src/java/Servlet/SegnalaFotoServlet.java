/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Ristorante;
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
public class SegnalaFotoServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());

        if (ristorante != null && utente != null && utente.proprietario(ristorante)) {
            String type = request.getParameter("type");
            if (type.equals("ristorante")) {
                manager.newNotSegnalaFotoRistorante(manager.getFoto(Integer.parseInt(request.getParameter("id_foto"))));
            } else if (type.equals("rec")) {
                manager.newNotSegnalaFotoRecensione(manager.getRecensione(Integer.parseInt(request.getParameter("id_rec"))));
            }
            request.setAttribute("segnalaMessageRist", labels.getString("foto.segnalata"));
        } else {
            request.setAttribute("message", labels.getString("not.prop.rist"));
        }

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
