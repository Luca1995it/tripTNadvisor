/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Ristorante;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SearchServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

    }

    @Override
    @SuppressWarnings("empty-statement")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (request.getParameter("tipo") == null || request.getParameter("ordine") == null
                || request.getParameter("fascia") == null || request.getParameter("spec") == null
                || request.getParameter("tipo").equals("") || request.getParameter("ordine").equals("")
                || request.getParameter("fascia").equals("") || request.getParameter("spec").equals("")) {

        } else {
            ArrayList<Ristorante> x;
            if ((x = (ArrayList<Ristorante>) session.getAttribute("originalResult")) != null) {
                ArrayList<Ristorante> res = (ArrayList<Ristorante>) x.clone();
                Comparator c;
                String tipo = (String) request.getParameter("tipo");
                String ordine = (String) request.getParameter("ordine");
                if (!(tipo == null || ordine == null)) {
                    switch (tipo) {
                        case "NoOrdine":
                            break;

                        case "pos":
                            c = (Comparator<Ristorante>) (Ristorante o1, Ristorante o2) -> {
                                int res1;
                                if (o1.getVoto() > o2.getVoto()) {
                                    res1 = -1;
                                } else if (o1.getVoto() < o2.getVoto()) {
                                    res1 = 1;
                                } else {
                                    res1 = 0;
                                }
                                if ("1".equals(ordine)) {
                                    return res1;
                                } else {
                                    return -res1;
                                }
                            };
                            res.sort(c);
                            break;
                        case "pre":
                            c = new Comparator<Ristorante>() {
                                int toInt(String s) {
                                    switch (s) {
                                        case "eco":
                                            return 1;
                                        case "mid":
                                            return 2;
                                        case "lux":
                                            return 3;
                                        default:
                                            return 0;
                                    }
                                }

                                @Override
                                public int compare(Ristorante o1, Ristorante o2) {
                                    int res;
                                    if (toInt(o1.getFascia()) > toInt(o2.getFascia())) {
                                        res = 1;
                                    } else if (toInt(o1.getFascia()) < toInt(o2.getFascia())) {
                                        res = -1;
                                    } else {
                                        res = 0;
                                    }
                                    if ("1".equals(ordine)) {
                                        return res;
                                    } else {
                                        return -res;
                                    }
                                }
                            };
                            res.sort(c);
                            break;
                        case "alf":
                            c = (Comparator<Ristorante>) (Ristorante o1, Ristorante o2) -> {
                                int res1 = o1.getNome().compareTo(o2.getNome());
                                if ("1".equals(ordine)) {
                                    return res1;
                                } else {
                                    return -res1;
                                }
                            };
                            res.sort(c);
                            break;
                    }
                }

                String fascia = (String) request.getParameter("fascia");
                String spec = (String) request.getParameter("spec");
                for (Iterator i = res.iterator(); i.hasNext();) {
                    Ristorante tmp = (Ristorante) i.next();
                    if (!"TuttiFascia".equals(fascia) && (!tmp.getFascia().equals(fascia))) {
                        i.remove();
                    } else if (!"all".equals(spec) && (!manager.similString(tmp.getCucina(), spec, 1))) {
                        i.remove();
                    }
                }
                session.removeAttribute("result");
                session.setAttribute("result", res);
            }
        }
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String research = request.getParameter("research");

        String tipo = request.getParameter("tipo");
        String spec = request.getParameter("spec");

        String lat = request.getParameter("Latitude");
        String lng = request.getParameter("Longitude");

        ArrayList<Ristorante> res;
        ArrayList<Ristorante> resOriginal;

        Language lan = (Language) session.getAttribute("lan");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + lan.getLanSelected());

        res = manager.search(research, tipo, spec, lat, lng, labels);

        resOriginal = (ArrayList<Ristorante>) res.clone();

        session.setAttribute("result", res);
        session.setAttribute("originalResult", resOriginal);
        request.getRequestDispatcher("/result.jsp").forward(request, response);

    }

}
