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
import java.sql.SQLException;
import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
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
        System.out.println(check);
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
            String cfr = encrypt(mail1);
            manager.addKey(user, cfr);
            sendMail(labels.getString("click.link.mail") + " http://localhost:2000" + request.getContextPath() + "/ConfirmServlet?hash=" + cfr, mail1, session);
            request.getRequestDispatcher("/HomeServlet").forward(request, response);
        } else {
            request.setAttribute("problemMessage", labels.getString("error.message"));
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
        }
    }

    public void sendMail(String message, String to, HttpSession session) throws IOException, MessagingException {

        Language lan = (Language) session.getAttribute("lan");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + lan.getLanSelected());

        String host = "smtp.gmail.com"; //mettere il vostro
        String from = ("triptnadvisor@gmail.com");
        String subject = (labels.getString("confirm.registration"));

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

    private static String encrypt(String rcf) throws UnsupportedEncodingException {
        String sha1;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(rcf.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        String result;
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            result = formatter.toString();
        }
        return result;
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
