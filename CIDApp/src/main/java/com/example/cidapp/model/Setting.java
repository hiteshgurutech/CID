package com.example.cidapp.model;

/**
 * Created by nilesh on 10/12/16.
 */
public class Setting {
    private String reg_enabled;
    private String forgot_password;
    private boolean two_factor_enabled;
    private String about_app,about_us,about_details;
    private String throttle_enabled;
    private String throttle_attempts;
    private String updateDate;
    public String getReg_enabled() {
        return reg_enabled;
    }

    public void setReg_enabled(String reg_enabled) {
        this.reg_enabled = reg_enabled;
    }

    public String getForgot_password() {
        return forgot_password;
    }

    public void setForgot_password(String forgot_password) {
        this.forgot_password = forgot_password;
    }

    public boolean isTwo_factor_enabled() {
        return two_factor_enabled;
    }

    public void setTwo_factor_enabled(boolean two_factor_enabled) {
        this.two_factor_enabled = two_factor_enabled;
    }

    public String getAbout_app() {
        return about_app;
    }

    public void setAbout_app(String about_app) {
        this.about_app = about_app;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getAbout_details() {
        return about_details;
    }

    public void setAbout_details(String about_details) {
        this.about_details = about_details;
    }

    public String getThrottle_enabled() {
        return throttle_enabled;
    }

    public void setThrottle_enabled(String throttle_enabled) {
        this.throttle_enabled = throttle_enabled;
    }

    public String getThrottle_attempts() {
        return throttle_attempts;
    }

    public void setThrottle_attempts(String throttle_attempts) {
        this.throttle_attempts = throttle_attempts;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
