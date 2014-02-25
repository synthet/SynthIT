package ru.synthet.synthit.model;

import android.content.Context;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import ru.synthet.synthit.model.service.RestService;

public final class RestRequestManager extends RequestManager {
    private RestRequestManager(Context context) {
        super(context, RestService.class);
    }

    private static RestRequestManager sInstance;

    public static RestRequestManager from(Context context) {
        if (sInstance == null) {
            sInstance = new RestRequestManager(context);
        }
        return sInstance;
    }
}
