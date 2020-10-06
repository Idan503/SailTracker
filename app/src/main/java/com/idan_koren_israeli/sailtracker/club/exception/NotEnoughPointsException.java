package com.idan_koren_israeli.sailtracker.club.exception;

public class NotEnoughPointsException extends Exception {

    public NotEnoughPointsException(String errorMessage){
        super(errorMessage);
    }
}
