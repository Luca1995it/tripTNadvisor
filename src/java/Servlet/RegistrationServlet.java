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
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Luca
 */
public class RegistrationServlet extends HttpServlet {

    @EJB
    private final EmailSessionBean emailSessionBean = new EmailSessionBean();

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, MessagingException {

        String surname = "Utente";
        String name = "Anonimo";
        String mail1 = request.getParameter("mail1");
        String mail2 = request.getParameter("mail2");
        String pass1 = request.getParameter("pass1");
        String pass2 = request.getParameter("pass2");
        String check = request.getParameter("check");

        boolean tornaIndietro = false;
        Utente user;
        HttpSession session = request.getSession();
        Language lan = (Language) session.getAttribute("lan");

        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + lan.getLanSelected());

        if (!(pass1.equals(pass2))) {
            request.setAttribute("passMessage", labels.getString("different.password"));
            tornaIndietro = true;
        } else if (pass1.length() < 8) {
            request.setAttribute("passMessage", labels.getString("char.password"));
            tornaIndietro = true;
        }

        if (!(mail1.equals(mail2))) {
            request.setAttribute("mailMessage", labels.getString("different.mail"));
            tornaIndietro = true;
        } else if (mail1.length() < 8 || !mail1.contains("@")) {
            request.setAttribute("mailMessage", labels.getString("valid.mail"));
            tornaIndietro = true;
        }

        boolean privacy = false;
        if(check != null) {
            privacy = true;
        }

        if (manager.esisteMail(mail1)) {
            request.setAttribute("doppioneMessage", labels.getString("double.mail"));
            tornaIndietro = true;
        }

        if (tornaIndietro) {
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
        } else if ((user = manager.addRegistrato(name, surname, mail1, pass1, privacy)) != null) {
            String cfr = EmailSessionBean.encrypt(mail1);
            manager.addKey(user, cfr);
            emailSessionBean.sendEmail(mail1, "Registration confirm", labels.getString("click.link.mail") + " https://" + manager.getCurrentIp().getHostAddress() + ":" + manager.port + request.getContextPath() + "/ConfirmServlet?hash=" + cfr);
            request.setAttribute("message", "Registrazione effettuata, controlla la mail");
            request.getRequestDispatcher("/HomeServlet").forward(request, response);
        } else {
            request.setAttribute("problemMessage", labels.getString("error.message"));
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
        }
        
    }

    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException | MessagingException ex) {
            Logger.getLogger(ex.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException | MessagingException ex) {
            Logger.getLogger(ex.toString());
        }
    }

}
