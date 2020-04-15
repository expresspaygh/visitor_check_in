package com.expresspay.access_control.models;

public class GuestItem extends ListItem {
    private GuestCheckedInData guestCheckedInData;

    public GuestCheckedInData getGuestCheckedInData() {
        return guestCheckedInData;
    }

    public void setGuestCheckedInData(GuestCheckedInData guestCheckedInData) {
        this.guestCheckedInData = guestCheckedInData;
    }

    @Override
    public int getType() {
        return TYPE_GUEST_DATA;
    }
}
