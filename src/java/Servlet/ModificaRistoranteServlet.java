/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Ristorante;
import DataBase.Utente;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucadiliello
 */
public class ModificaRistoranteServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        response.setContentType("text/plain"); //tipo di file di upload
        Utente utente = (Utente) session.getAttribute("utente");

        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");

        String newNome = ristorante.getNome();
        String newAddress = ristorante.getLuogo().getAddress();
        String newDescr = ristorante.getDescr();
        String newLinksito = ristorante.getLinksito();
        String newFascia = ristorante.getFascia();
        String newCucina = ristorante.getCucina();

        boolean tornaIndietro = false;

        if (request.getParameter("nome").length() == 0); else if (request.getParameter("nome").length() < 2) {
            tornaIndietro = true;
            request.setAttribute("message", "Nome troppo corto");
        } else {
            newNome = request.getParameter("nome");
        }

        if (request.getParameter("address").length() == 0); else if (!manager.okLuogo(request.getParameter("address"))) {
            tornaIndietro = true;
            request.setAttribute("message", "Indirizzo invalido, provare con via, citta', CAP, stato");
        } else {
            newAddress = request.getParameter("address");
        }

        if (request.getParameter("descr").length() == 0); else if (request.getParameter("descr").length() < 50) {
            request.setAttribute("message", "La descrizione è troppo breve");
            tornaIndietro = true;
        } else {
            newDescr = request.getParameter("descr");
        }

        if (request.getParameter("linksito").length() != 0) {
            newLinksito = request.getParameter("linksito");
        }

        if (!request.getParameter("cucina").equals("nothing")) {
            newCucina = request.getParameter("cucina");
        }

        if (!request.getParameter("fascia").equals("nothing")) {
            newFascia = request.getParameter("fascia");
        }

        if (tornaIndietro) {
            request.getRequestDispatcher("/privateRistoratore/modificaRist.jsp").forward(request, response);
        } else {
            ristorante.updateData(newNome, newAddress, newLinksito, newDescr, newCucina, newFascia);
        }

        request.getRequestDispatcher("/privateRistoratore/ConfigurazioneRistoranti").forward(request, response);
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
