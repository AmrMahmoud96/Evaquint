package com.evaquint.android.utils.code;

/**
 * Created by henry on 8/15/2017.
 */

public enum DatabaseValues {
    USER_TABLE ("users", 1),
    CONTACTS_TABLE ("contacts", 2),
    EVENTS_TABLE ("events", 3),
    EVENTS_GEOFIRE_TABLE("eventsGeofire", 4),
    USERS_GEOFIRE_TABLE("usersGeofire", 5),
    CATEGORIES_TABLE("categories", 6);

    private final String name;
    private final int code;

    private DatabaseValues(String s, int i) {
        name = s;
        code = i;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String getName() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }
}

