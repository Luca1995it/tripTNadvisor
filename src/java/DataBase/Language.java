/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;
/**
 *
 * @author bazza
 */
public class Language {

    private String lanSelected;
    
    public Language() {
        this.lanSelected = "en_GB";
    }

    /**
     * @return the firstLanguage
     */
    public String getLanSelected() {
        return lanSelected;
    }

    /**
     * Set en_GB as language
     */
    public void setDefault(){
        lanSelected = "en_GB";
    }
    /**
     * @param lanSelected
     */
    public void setLanSelected(String lanSelected) {
        this.lanSelected = lanSelected;
    }

}
