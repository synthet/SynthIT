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
			JSONArray tweetsJson = new JSONArray(result.body);
			tweetsValues = new ContentValues[tweetsJson.length()];
			for (int i = 0; i < tweetsJson.length(); ++i) {
                String password = tweetsJson.getJSONObject(i).getString("password");
                if ((password.length() > 0) && !(password.equals("null"))) {
                    ContentValues tweet = new ContentValues();
                    tweet.put("uid", tweetsJson.getJSONObject(i).getString("uid"));
                    tweet.put("dn", tweetsJson.getJSONObject(i).getString("dn"));
                    tweet.put("displayName", tweetsJson.getJSONObject(i).getString("displayName"));
                    tweet.put("description", tweetsJson.getJSONObject(i).getString("description"));
                    tweet.put("password", password);
                    tweetsValues[i] = tweet;
                }
			}
		} catch (JSONException e) {
			throw new DataException(e.getMessage());
		}
		
		context.getContentResolver().delete(Contract.Comps.CONTENT_URI, null, null);
		context.getContentResolver().bulkInsert(Contract.Comps.CONTENT_URI, tweetsValues);
		return null;
	}
	
}
