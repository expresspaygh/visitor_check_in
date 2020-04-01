package com.expresspay.access_control.models;

import java.io.Serializable;

public class CheckInData implements Serializable {

    private String visitorName;
    private String visitorPhone;
    private String staffName;
    private String purpose;
    private String passNumber;

    public CheckInData(String visitorName, String visitorPhone, String staffName, String purpose, String passNumber) {
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
        this.staffName = staffName;
        this.purpose = purpose;
        this.passNumber = passNumber;
    }

    public CheckInData() {
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }
}
