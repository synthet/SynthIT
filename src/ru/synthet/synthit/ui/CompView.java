package ru.synthet.synthit.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.CompsArrayProvider;
import ru.synthet.synthit.model.provider.Contract;

import java.util.ArrayList;

public class CompView extends FragmentActivity {

    private static final int LOADER_ID = 3;

    private static final String[] COLUMNS = {
            Contract.Comps.NAME,
            Contract.Comps.UID,
            Contract.Comps.TAG,
            Contract.Comps.DESC,
            Contract.Comps.OS,
            Contract.Comps.OSNAME,
            Contract.Comps.OSCOMMENTS,
            Contract.Comps.IPADDR,
            Contract.Comps.DEFAULTGATEWAY,
            Contract.Comps.IPGATEWAY,
            Contract.Comps.IPMASK,
            Contract.Comps.MACADDR,
            Contract.Comps.MEMORY,
            Contract.Comps.MEMORYSIZE,
            Contract.Comps.MEMORYTYPE,
            Contract.Comps.MEMORYH,
            Contract.Comps.PROCESSORT,
            Contract.Comps.PROCESSORN,
            Contract.Comps.PROCESSORS
    };

    protected ListView listView;
    protected SimpleCursorAdapter adapter;

    private Integer id = 0;

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
            return new CursorLoader(
                    CompView.this,
                    Contract.Comps.CONTENT_URI,
                    null,
                    Contract.Comps._ID + "= ? ",
                    new String[]{ String.valueOf(id) },
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            Integer count = cursor.getCount();
            if (count == 1) {
                cursor.moveToFirst();
                //String[] values = new String[COLUMNS.length];
                ArrayList<String> values = new ArrayList<String>();
                ArrayList<String> rows = new ArrayList<String>();
                for(int i=0; i<COLUMNS.length; i++) {
                    String value = cursor.getString(cursor.getColumnIndex(COLUMNS[i]));
                    if ((value.length()>0) && (!value.equals("null"))) {
                        values.add(value);
                        rows.add(COLUMNS[i]);
                    }
                }
                CompsArrayProvider adapter = new CompsArrayProvider(CompView.this, rows, values);
                listView.setAdapter(adapter);
            }
            //cursor.close();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {

        }


    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comps_view);

        Intent intent = getIntent();
        id = intent.getIntExtra("Comp_ID", -1);


        listView = (ListView) findViewById(R.id.listView);


        getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
    }

}