package Listener;

import DataBase.DBManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Luca
 */
public class WebAppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String dburl = sce.getServletContext().getInitParameter("dburl");
        String completePath = sce.getServletContext().getInitParameter("completePath");
        String photoFolder = sce.getServletContext().getInitParameter("photoFolder");
        String user = sce.getServletContext().getInitParameter("user");
        String password = sce.getServletContext().getInitParameter("password");
        int port = Integer.parseInt(sce.getServletContext().getInitParameter("port"));
        
        DBManager manager = new DBManager(port, dburl, user, password, completePath, photoFolder);
        manager.updateAutocomplete();
        sce.getServletContext().setAttribute("dbmanager", manager);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            DBManager.shutdown();
            //niente da fare
        } catch (Throwable ex) {
            System.out.println("Failed to stop DataBase: " + ex.toString());
        }
    }

}
