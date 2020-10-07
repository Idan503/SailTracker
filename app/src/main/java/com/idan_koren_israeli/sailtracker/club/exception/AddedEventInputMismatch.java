package com.idan_koren_israeli.sailtracker.club.exception;

import java.util.InputMismatchException;

public class AddedEventInputMismatch extends InputMismatchException {

    private int menuId; // in which menu of the viewflipper is the mismatch
    public AddedEventInputMismatch(String error, int menuId){
        super(error);
        this.menuId = menuId;

    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
