package com.expresspay.access_control.models;

public class GuestData {

    private String fullName;
    private String checkedInTime;
    private String checkedOutTime;
    private boolean checkedOut;

    public GuestData(String fullName, String checkedInTime, String checkedOutTime, boolean checkedOut) {
        this.fullName = fullName;
        this.checkedInTime = checkedInTime;
        this.checkedOutTime = checkedOutTime;
        this.checkedOut = checkedOut;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCheckedInTime() {
        return checkedInTime;
    }

    public String getCheckedOutTime() {
        return checkedOutTime;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCheckedInTime(String checkedInTime) {
        this.checkedInTime = checkedInTime;
    }

    public void setCheckedOutTime(String checkedOutTime) {
        this.checkedOutTime = checkedOutTime;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }
}
