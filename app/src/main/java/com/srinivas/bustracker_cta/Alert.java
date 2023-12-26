package com.srinivas.bustracker_cta;

import java.io.Serializable;

public class Alert implements Serializable {
    private String alertId;
    private String headline;
    private String data;

    public Alert(String alertId, String headline, String data) {
        this.alertId = alertId;
        this.headline = headline;
        this.data = data;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
