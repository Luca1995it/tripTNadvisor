/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.io.Serializable;

/**
 *
 * @author Luca
 */
public class Luogo implements Serializable {

    private final double lat;
    private final double lng;
    private final int street_number;
    private final String street;
    private final String city;
    private final String area1;
    private final String area2;
    private final String state;
    private final int id;

    public int getStreet_number() {
        return street_number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getArea1() {
        return area1;
    }

    public String getArea2() {
        return area2;
    }

    public String getState() {
        return state;
    }

    public int getId() {
        return id;
    }


    public Luogo(int id, double lat, double lng, int street_number, String street, String city, String area1, String area2, String state) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.street_number = street_number;
        this.street = street;
        this.city = city;
        this.area1 = area1;
        this.area2 = area2;
        this.state = state;
    }

    public String getAddress() {
        return street + " " + street_number + ", " + city + ", " + area1 + ", " + area2 + ", " + state;
    }
    
    public String getGeographicZone(){
        return area1 + ", " + area2 + ", " + state;
    }
    
    public String getSmallZone(){
        return street + " " + street_number + ", " + city;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
