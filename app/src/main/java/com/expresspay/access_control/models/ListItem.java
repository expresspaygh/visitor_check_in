package com.expresspay.access_control.models;

public abstract class ListItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_GUEST_DATA = 1;

    abstract public int getType();

}
