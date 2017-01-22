/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Notify.Notifica;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucadiliello
 */
public class ApplicaNotificaServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String sc = request.getParameter("accept");
        String id = request.getParameter("id_not");
        ArrayList<Notifica> res = (ArrayList<Notifica>) session.getAttribute("notifiche");
        if (id == null || sc == null || res == null) {
            request.setAttribute("errMessageNotifica", "errore applicazione notifica, riprova");
        } else {
            boolean scelta = Boolean.parseBoolean(sc);
            int id_not = Integer.parseInt(request.getParameter("id_not"));

            Notifica notifica = res.get(searchArrayList(res, id_not));

            if (scelta) {
                notifica.accetta();
            } else {
                notifica.rifiuta();
            }

            res.remove(notifica);
            session.setAttribute("notifiche", res);
        }

        request.getRequestDispatcher("/privateRistoratore/notifiche.jsp").forward(request, response);
    }

    int searchArrayList(ArrayList<Notifica> r, int id_not) {
        int i = 0;
        for (Notifica n : r) {
            if (n.getId() == id_not) {
                return i;
            }
            i++;
        }
        return -1;
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
