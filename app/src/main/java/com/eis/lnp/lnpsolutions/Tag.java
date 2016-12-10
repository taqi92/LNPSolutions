package com.eis.lnp.lnpsolutions;

import java.util.ArrayList;

public class Tag {

    public int id;
    public ArrayList<String> images;
    public ArrayList<Integer> blocked;
    public String land_type;
    public String land_size;
    public String farmer_name;
    public String farmer_phone;
    public String harvest;
    public String lat;
    public String lon;

    public Tag( ArrayList<String> imgs, String landT, String landS, String farmerN, String farmerP, String Harv, String lati, String loni, ArrayList<Integer> bImags){
        blocked=bImags;
        images=imgs;
        land_type=landT;
        land_size=landS;
        farmer_name=farmerN;
        farmer_phone=farmerP;
        harvest=Harv;
        lat=lati;
        lon=loni;
    }

    public void setID(int i){
        id=i;
    }
}
