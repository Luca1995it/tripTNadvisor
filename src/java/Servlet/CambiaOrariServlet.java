/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Ristorante;
import java.io.IOException;
import java.sql.Time;
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
public class CambiaOrariServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id_times = Integer.parseInt(request.getParameter("id_orario"));
        HttpSession session = request.getSession();
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");
        if (ristorante != null) {
            ristorante.removeTimes(id_times);
            request.getRequestDispatcher("/privateRistoratore/orari.jsp").forward(request, response);
        } else {
            request.setAttribute("errOrario", "Errore interno, riprova");
            request.getRequestDispatcher("/privateRistoratore/orari.jsp").forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Ristorante ristorante = (Ristorante) session.getAttribute("ristorante");
        if (ristorante != null) {
            int numero = Integer.parseInt(request.getParameter("numin"));

            for (int i = 0; i < numero; i++) {
                if (!(request.getParameter("apH" + i) == null || request.getParameter("apM" + i) == null)) {
                    if (!(request.getParameter("apH" + i).equals("") || request.getParameter("apM" + i).equals(""))) {
                        Time apertura = new Time(Integer.parseInt(request.getParameter("apH" + i)), Integer.parseInt(request.getParameter("apM" + i)), 0);
                        Time chiusura = new Time(Integer.parseInt(request.getParameter("chH" + i)), Integer.parseInt(request.getParameter("chM" + i)), 0);
                        int day = Integer.parseInt(request.getParameter("day" + i));
                        ristorante.addTimesToRistorante(day, apertura, chiusura);
                    }
                }

            }
        }
        request.getRequestDispatcher("/privateRistoratore/orari.jsp").forward(request, response);
    }

}
