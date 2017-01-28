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
public final class Language {

    private String lanSelected;
    private final String [] language = {"it_IT","en_GB"};

    public String[] getLanguage() {
        return language;
    }
    
    
    public Language() {
        setDefault();
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
        lanSelected = language[0];
    }
    /**
     * @param lanSelected
     */
    public void setLanSelected(String lanSelected) {
        this.lanSelected = lanSelected;
    }

}
