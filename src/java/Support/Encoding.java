/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import Mail.EmailSessionBean;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author lucadiliello
 */
public class Encoding {

    private static int counter = 0;

    public static int getNewCode() {
        return counter++;
    }

    public static String Encode(String filename) throws UnsupportedEncodingException {

        int dot = filename.lastIndexOf(".");
        String ext = filename.substring(dot);
        String name = filename.substring(dot, filename.length());
        String newname;

        newname = (name + (new GregorianCalendar() {}).toString() + (new GregorianCalendar() {}).getTime() + EmailSessionBean.encrypt(name) + Encoding.getNewCode()).replace(".", "").replace(" ", "_").replace(":", "-") + ext;
        return newname;

    }
}
