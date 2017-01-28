/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

/**
 *
 * @author lucadiliello
 */
public class Encoding {
    private static int counter = 0;
    
    public static int getNewCode(){
        return counter++;
    }
}
