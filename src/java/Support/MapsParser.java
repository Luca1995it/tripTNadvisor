/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import static DataBase.DBManager.readJsonFromUrl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author lucadiliello
 */
public class MapsParser {

    JSONObject json;

    public MapsParser(String address, String googleKey) throws InvalidAddresException {
        String req = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(' ', '+') + "&key=" + googleKey;
        try {
            json = readJsonFromUrl(req);
        } catch (IOException | JSONException ex) {
            throw new InvalidAddresException();
        }
    }

    public String getState() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                    return faddress.getJSONObject(i).getString("long_name");
                }
            }
        } catch (JSONException ex) {
            return null;
        }
        return null;
    }

    public String getArea1() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_1")) {
                    return faddress.getJSONObject(i).getString("long_name");
                }
            }
        } catch (JSONException ex) {
            return null;
        }
        return null;
    }

    public String getArea2() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                    return faddress.getJSONObject(i).getString("long_name");
                }
            }
        } catch (JSONException ex) {
            return null;
        }
        return null;
    }

    public String getCity() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("locality")) {
                    return faddress.getJSONObject(i).getString("long_name");
                }
            }
        } catch (JSONException ex) {
            return null;
        }
        return null;
    }

    public String getStreet() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("route")) {
                    return faddress.getJSONObject(i).getString("long_name");
                }
            }
        } catch (JSONException ex) {
            return null;
        }
        return null;
    }

    public int getStreetNumber() {
        try {
            JSONArray faddress = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < faddress.length(); i++) {
                if (faddress.getJSONObject(i).getJSONArray("types").getString(0).equals("street_number")) {
                    return faddress.getJSONObject(i).getInt("long_name");
                }
            }
        } catch (JSONException ex) {
            return -1;
        }
        return -1;
    }

    public double getLat() {
        try {
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            return location.getDouble("lat");
        } catch (JSONException ex) {
            Logger.getLogger(MapsParser.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public double getLng() {
        try {
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            return location.getDouble("lng");
        } catch (JSONException ex) {
            Logger.getLogger(MapsParser.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

}
