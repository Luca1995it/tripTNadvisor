/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Ristorante;
import java.io.IOException;
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
public class CambiaCucinaServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String spec = request.getParameter("spec");
        HttpSession session = request.getSession();
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");
        
        if (ristorante != null) {
            ristorante.removeCucina(spec);
        } else {
            request.setAttribute("errSpec", "Errore interno, riprova");
        }
        request.getRequestDispatcher("/privateRistoratore/spec.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");
        String spec = request.getParameter("spec");

        if (ristorante != null) {
            ristorante.addCucina(spec);
        } else {
            request.setAttribute("errSpec", "Errore interno, riprova");
        }
        request.getRequestDispatcher("/privateRistoratore/spec.jsp").forward(request, response);
    }

}
