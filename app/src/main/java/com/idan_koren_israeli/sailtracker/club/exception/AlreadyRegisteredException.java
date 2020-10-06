package com.idan_koren_israeli.sailtracker.club.exception;

public class AlreadyRegisteredException extends Exception {

    public AlreadyRegisteredException(String errorMessage){
        super(errorMessage);
    }
}
