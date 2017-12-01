package com.example.cidapp.model;

import java.io.Serializable;

/**
 * Created by nilesh on 16/1/17.
 */
public class PhoneModel implements Serializable{
    String userId,phoneId,phoneNumber,phoneCountryCode,phoneIMEI,phoneStatus,phoneSMSToken,phoneLat,phoneLng,phoneAddress;
    String os_api_level,device,model,manufacturer,brand,display,os_version;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneIMEI() {
        return phoneIMEI;
    }

    public void setPhoneIMEI(String phoneIMEI) {
        this.phoneIMEI = phoneIMEI;
    }

    public String getPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public String getPhoneSMSToken() {
        return phoneSMSToken;
    }

    public void setPhoneSMSToken(String phoneSMSToken) {
        this.phoneSMSToken = phoneSMSToken;
    }

    public String getPhoneLat() {
        return phoneLat;
    }

    public void setPhoneLat(String phoneLat) {
        this.phoneLat = phoneLat;
    }

    public String getPhoneLng() {
        return phoneLng;
    }

    public void setPhoneLng(String phoneLng) {
        this.phoneLng = phoneLng;
    }

    public String getPhoneAddress() {
        return phoneAddress;
    }

    public void setPhoneAddress(String phoneAddress) {
        this.phoneAddress = phoneAddress;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getOs_api_level() {
        return os_api_level;
    }

    public void setOs_api_level(String os_api_level) {
        this.os_api_level = os_api_level;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }
}
