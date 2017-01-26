/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Utente;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucadiliello
 */
public class NuovaPassword extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        try {
            String mail = request.getParameter("mail");
            Utente utente = manager.getUtente(mail);

            String newPass = generatePassword(8);
            if (utente != null) {
                if (utente.updatePassword(utente.getPassword(),newPass)) {
                    sendMail("Nuova password: " + newPass, utente.getEmail(), session);
                }
            }
        } catch (NullPointerException | MessagingException ex) {
            Logger.getLogger(NuovaPassword.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String generatePassword(int length) {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, length);
    }

    public void sendMail(String message, String to, HttpSession session) throws IOException, MessagingException {

        Language lan = (Language) session.getAttribute("lan");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + lan.getLanSelected());

        String host = "smtp.gmail.com"; //mettere il vostro
        String from = ("triptnadvisor@gmail.com");
        String subject = (labels.getString("new.password"));

        // Here we listen indefinetely to the incoming requests
        boolean sessionDebug = false;
        //Use here the code introduced in the previous slides
        //for the case of sending e-mails with Gmail.
        final String username = "triptnadvisor@gmail.com";
        final String password = "adminadmin";
        // Get a Properties object to set the mailing configuration
        // parameters
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        //We create the session object with the authentication information
        Session mailsession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        //Create a new message
        Message msg = new MimeMessage(mailsession);
        //Set the FROM and TO fields –
        msg.setFrom(new InternetAddress(from + ""));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);
        msg.setText(message);
        msg.setSentDate(new Date());

        Transport transport = mailsession.getTransport("smtps");
        transport.connect(host, 465, username, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
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
