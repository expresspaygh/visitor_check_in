package com.expresspay.access_control.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GuestCheckedInData extends RealmObject {
    private String visitorName;
    private String visitorPhone;
    private String staffName;
    private String purpose;
    private String passNumber;
    @PrimaryKey
    private String checkedInTime;

    private String checkedOutTime;
    private boolean checkedOut;

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

    public String getCheckedInTime() {
        return checkedInTime;
    }

    public void setCheckedInTime(String checkedInTime) {
        this.checkedInTime = checkedInTime;
    }

    public String getCheckedOutTime() {
        return checkedOutTime;
    }

    public void setCheckedOutTime(String checkedOutTime) {
        this.checkedOutTime = checkedOutTime;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }
}
