package ru.synthet.synthit.model.operations;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import org.json.JSONArray;
import org.json.JSONException;
import ru.synthet.synthit.model.provider.Contract;

import java.util.HashMap;

public final class CompsOperation implements Operation {
	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		NetworkConnection connection = new NetworkConnection(context, "http://10.1.2.12/ocs/index.php/ad/users2");
		HashMap<String, String> params = new HashMap<String, String>();
		//params.put("screen_name", request.getString("screen_name"));
		connection.setParameters(params);
		NetworkConnection.ConnectionResult result = connection.execute();
		ContentValues[] tweetsValues;
		try {
			JSONArray jsonArray = new JSONArray(result.body);
			tweetsValues = new ContentValues[jsonArray.length()];
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
                    tweetsValues[i] = values;
                }
			}
		} catch (JSONException e) {
            //Log.d()
            throw new DataException(e.getMessage());
		}

        if (tweetsValues.length > 0) {
		    context.getContentResolver().delete(Contract.Comps.CONTENT_URI, null, null);
		    context.getContentResolver().bulkInsert(Contract.Comps.CONTENT_URI, tweetsValues);
        }
		return null;
	}
	
}
