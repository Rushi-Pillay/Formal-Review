package com.example.login;

public class Specials {
    private int specID;
    private String specName;
    private String specDesc;
    private int specIMG;

    public int getSpecID() {
        return specID;
    }

    public String getSpecName() {
        return specName;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public int getSpecIMG() {
        return specIMG;
    }

    public Specials(int specID, String specName, String specDesc, int specIMG) {
        this.specID = specID;
        this.specName = specName;
        this.specDesc = specDesc;
        this.specIMG = specIMG;
    }


}
