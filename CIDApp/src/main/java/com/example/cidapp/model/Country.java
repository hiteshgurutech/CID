package com.example.cidapp.model;

import java.io.Serializable;

/**
 * Created by nilesh on 10/12/16.
 */
public class Country implements Serializable
{
    private String id,countryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
