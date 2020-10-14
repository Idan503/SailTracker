package com.idan_koren_israeli.sailtracker.recycler;

public enum EventRecyclerType {
    DEFAULT, // showing info only (base adapter)
    SEPARATED, // used for history
    REGISTER, // default calendar recycler for regular user
    MANAGER // register + add item functionality (for managers)

}
