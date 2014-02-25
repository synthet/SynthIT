package ru.synthet.synthit.model;

import com.foxykeep.datadroid.requestmanager.Request;

public final class RequestFactory {
    public static final int REQUEST_USERS = 1;
    public static final int REQUEST_COMPS = 2;

    public static Request getUsersRequest() {
        return new Request(REQUEST_USERS);
    }

    public static Request getCompsRequest() {
        return new Request(REQUEST_COMPS);
    }

    private RequestFactory() {
    }
}
