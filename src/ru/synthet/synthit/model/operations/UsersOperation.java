package ru.synthet.synthit.model.operations;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import org.json.JSONArray;
import org.json.JSONException;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.provider.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class UsersOperation implements Operation {
    @Override
    public Bundle execute(Context context, Request request)
            throws ConnectionException, DataException, CustomRequestException {
        NetworkConnection connection = new NetworkConnection(context, context.getResources().getString(R.string.url_users));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("password", context.getResources().getString(R.string.password));
        connection.setParameters(params);
        NetworkConnection.ConnectionResult result = connection.execute();
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        try {
            JSONArray jsonArray = new JSONArray(result.body);
            for (int i = 0; i < jsonArray.length(); ++i) {
                String password = jsonArray.getJSONObject(i).getString(Contract.Users.PASSWORD);
                String password_ad = "";
                if (jsonArray.getJSONObject(i).has(Contract.Users.PASSWORD_AD))
                    password_ad = jsonArray.getJSONObject(i).getString(Contract.Users.PASSWORD_AD);
                if (((password.length() > 0) && !(password.equals("null"))) ||
                        ((password_ad.length() > 0) && !(password_ad.equals("null")))) {
                    ContentValues values = new ContentValues();
                    values.put(Contract.Users.UID, jsonArray.getJSONObject(i).getString(Contract.Users.UID));
                    values.put(Contract.Users.DN, jsonArray.getJSONObject(i).getString(Contract.Users.DN));
                    String displayName = jsonArray.getJSONObject(i).getString(Contract.Users.DISPLAY_NAME);
                    values.put(Contract.Users.DISPLAY_NAME, displayName);
                    values.put(Contract.Users.DISPLAY_NAME_UP, displayName.toUpperCase());
                    values.put(Contract.Users.DESCRIPTION, jsonArray.getJSONObject(i).getString(Contract.Users.DESCRIPTION));
                    values.put(Contract.Users.PASSWORD, password);
                    values.put(Contract.Users.PASSWORD_AD, password_ad);
                    contentValues.add(values);
                }
            }
        } catch (JSONException e) {
            //Log.d()
            throw new DataException(e.getMessage());
        }

        context.getContentResolver().delete(Contract.Users.CONTENT_URI, null, null);
        context.getContentResolver().bulkInsert(Contract.Users.CONTENT_URI, contentValues.toArray(new ContentValues[contentValues.size()]));
        return null;
    }

}
