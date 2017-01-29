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
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author lucadiliello
 */
public class NuovaPassword extends HttpServlet {

    @EJB
    private final EmailSessionBean emailSessionBean = new EmailSessionBean();

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
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + ((Language) session.getAttribute("lan")).getLanSelected());

        try {
            String mail = request.getParameter("mail");
            Utente utente = manager.getUtente(mail);

            String newPass = generatePassword(8);
            if (utente != null) {
                if (utente.updatePassword(utente.getPassword(), newPass)) {
                    emailSessionBean.sendEmail(utente.getEmail(), labels.getString("new.pass.mail"), labels.getString("new.password")  + newPass);
                }
            }
        } catch (NullPointerException ex) {
            Logger.getLogger(NuovaPassword.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String generatePassword(int length) {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, length);
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
