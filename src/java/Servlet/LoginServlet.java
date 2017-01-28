package Servlet;

import DataBase.DBManager;
import DataBase.Language;
import DataBase.Utente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        // inizializza il DBManager dagli attributi di Application
        this.manager = (DBManager) super.getServletContext().getAttribute("dbmanager");
    }

    protected void doGetPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ServletException, SQLException {
        String mail = req.getParameter("mail");
        String password = req.getParameter("password");
        HttpSession session = req.getSession();
        
        Language lan = (Language) session.getAttribute("lan");
        ResourceBundle labels = ResourceBundle.getBundle("Resources.string_" + lan.getLanSelected());

        if ((mail == null) || (password == null)) {
            req.setAttribute("message", labels.getString("insert.mail.password"));
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } else {

            Utente utente = manager.authenticate(mail, password);

            if (utente == null) {
                req.setAttribute("message", labels.getString("err.mail.password"));
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            } else if (!utente.isActivate()) {
                req.setAttribute("message", labels.getString("err.activate.account"));
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            } else if(!utente.isAccepted()) {
                session.setAttribute("utenteTmp", utente);
                req.getRequestDispatcher("/privacy.jsp").forward(req, resp);
            } else {
                session.setAttribute("utente", utente);
                req.getRequestDispatcher("/HomeServlet").forward(req, resp);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doGetPost(req, resp);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doGetPost(req, resp);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
