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

public final class CompsOperation implements Operation {
    @Override
    public Bundle execute(Context context, Request request)
            throws ConnectionException, DataException, CustomRequestException {
        NetworkConnection connection = new NetworkConnection(context, context.getResources().getString(R.string.url_comps));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("password", context.getResources().getString(R.string.password));
        connection.setParameters(params);
        NetworkConnection.ConnectionResult result = connection.execute();
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        try {
            JSONArray jsonArray = new JSONArray(result.body);
            for (int i = 0; i < jsonArray.length(); ++i) {
                // TODO: actual info
                ContentValues values = new ContentValues();
                values.put(Contract.Comps.ID, jsonArray.getJSONObject(i).getString("ID"));
                values.put(Contract.Comps.NAME, jsonArray.getJSONObject(i).getString("NAME").toLowerCase());
                values.put(Contract.Comps.OSNAME, jsonArray.getJSONObject(i).getString("OSNAME"));
                values.put(Contract.Comps.OSCOMMENTS, jsonArray.getJSONObject(i).getString("OSCOMMENTS"));
                values.put(Contract.Comps.PROCESSORT, jsonArray.getJSONObject(i).getString("PROCESSORT"));
                values.put(Contract.Comps.PROCESSORS, jsonArray.getJSONObject(i).getString("PROCESSORS"));
                values.put(Contract.Comps.PROCESSORN, jsonArray.getJSONObject(i).getString("PROCESSORN"));
                values.put(Contract.Comps.MEMORY, jsonArray.getJSONObject(i).getString("MEMORY"));
                values.put(Contract.Comps.IPADDR, jsonArray.getJSONObject(i).getString("IPADDR"));
                values.put(Contract.Comps.DNS, jsonArray.getJSONObject(i).getString("DNS"));
                values.put(Contract.Comps.DEFAULTGATEWAY, jsonArray.getJSONObject(i).getString("DEFAULTGATEWAY"));
                values.put(Contract.Comps.USERID, jsonArray.getJSONObject(i).getString("USERID"));
                values.put(Contract.Comps.MACADDR, jsonArray.getJSONObject(i).getString("MACADDR"));
                values.put(Contract.Comps.IPGATEWAY, jsonArray.getJSONObject(i).getString("IPGATEWAY"));
                values.put(Contract.Comps.IPMASK, jsonArray.getJSONObject(i).getString("IPMASK"));
                values.put(Contract.Comps.TAG, jsonArray.getJSONObject(i).getString("TAG"));
                values.put(Contract.Comps.MEMORYTYPE, jsonArray.getJSONObject(i).getString("MEMORYTYPE"));
                values.put(Contract.Comps.MEMORYSIZE, jsonArray.getJSONObject(i).getString("MEMORYSIZE"));
                values.put(Contract.Comps.MEMORYH, jsonArray.getJSONObject(i).getString("MEMORYH"));
                values.put(Contract.Comps.DESC, jsonArray.getJSONObject(i).getString("DESC"));
                values.put(Contract.Comps.OS, jsonArray.getJSONObject(i).getString("OS"));
                values.put(Contract.Comps.UID, jsonArray.getJSONObject(i).getString("UID"));
                contentValues.add(values);
            }
        } catch (JSONException e) {
            //Log.d()
            throw new DataException(e.getMessage());
        }

        context.getContentResolver().delete(Contract.Comps.CONTENT_URI, null, null);
        context.getContentResolver().bulkInsert(Contract.Comps.CONTENT_URI, contentValues.toArray(new ContentValues[contentValues.size()]));
        return null;
    }

}
