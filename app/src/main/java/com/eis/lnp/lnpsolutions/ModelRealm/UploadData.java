package com.eis.lnp.lnpsolutions.ModelRealm;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HP on 12/8/2016.
 */

public class UploadData extends RealmObject {

    @PrimaryKey
    private int id;
    private String sLand;
    private String tLand;
    private String fName;
    private String pName;
    private String harvest;
    private String lat;
    private String lan;

    private String img;

    public UploadData() {
    }

    public UploadData(int id, String sLand, String tLand, String fName, String pName, String harvest, String lat, String lan, String img) {
        this.id = id;
        this.sLand = sLand;
        this.tLand = tLand;
        this.fName = fName;
        this.pName = pName;
        this.harvest = harvest;
        this.lat = lat;
        this.lan = lan;
        this.img = img;
    }

   /* public UploadData(int i, String s, String s1, String s2, String s3, String s4, String lat, String lon, String imageData) {
    }*/

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getsLand() {
        return sLand;
    }

    public void setsLand(String sLand) {
        this.sLand = sLand;
    }

    public String gettLand() {
        return tLand;
    }

    public void settLand(String tLand) {

    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getHarvest() {
        return harvest;
    }

    public void setHarvest(String harvest) {
        this.harvest = harvest;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
