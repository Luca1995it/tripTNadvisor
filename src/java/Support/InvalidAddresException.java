/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

public class InvalidAddresException extends Exception {

    @Override
    public String toString() {
        return "Impossible to parse with google maps the requested address ";
    }
}
