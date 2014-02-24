package ru.synthet.synthit.model.operations;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.provider.Contract;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;

public final class UsersOperation implements Operation {
	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		NetworkConnection connection = new NetworkConnection(context, context.getResources().getString(R.string.url_users));
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("screen_name", request.getString("screen_name"));
        connection.setParameters(params);
        NetworkConnection.ConnectionResult result = connection.execute();
        ContentValues[] contentValues;
        try {
            JSONArray jsonArray = new JSONArray(result.body);
            contentValues = new ContentValues[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                String password = jsonArray.getJSONObject(i).getString("password");
                String password_ad = "";
                if (jsonArray.getJSONObject(i).has("password_ad"))
                    password_ad = jsonArray.getJSONObject(i).getString("password_ad");
                if (((password.length() > 0) && !(password.equals("null"))) ||
                        ((password_ad.length() > 0) && !(password_ad.equals("null")))) {
                    ContentValues values = new ContentValues();
                    values.put("uid", jsonArray.getJSONObject(i).getString("uid"));
                    values.put("dn", jsonArray.getJSONObject(i).getString("dn"));
                    values.put("displayName", jsonArray.getJSONObject(i).getString("displayName"));
                    values.put("description", jsonArray.getJSONObject(i).getString("description"));
                    values.put("password", password);
                    values.put("password_ad", password_ad);
                    contentValues[i] = values;
                }
            }
        } catch (JSONException e) {
            //Log.d()
            throw new DataException(e.getMessage());
        }
		
		context.getContentResolver().delete(Contract.Users.CONTENT_URI, null, null);
		context.getContentResolver().bulkInsert(Contract.Users.CONTENT_URI, contentValues);
		return null;
	}
	
}
