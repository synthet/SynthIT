package ru.synthet.synthit.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.foxykeep.datadroid.requestmanager.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RequestFactory;
import ru.synthet.synthit.model.provider.Contract;

public class CompsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int LOADER_ID = 2;

    private static final String[] PROJECTION = {
            Contract.Comps._ID,
            Contract.Comps.UID,
            Contract.Comps.IPADDR,
            Contract.Comps.ID,
            Contract.Comps.OS,
            Contract.Comps.MACADDR,
            Contract.Comps.TAG,
            Contract.Comps.NAME
    };

    private LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
            if (mCurFilter == null) {
                return new CursorLoader(
                        CompsActivity.this,
                        Contract.Comps.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null
                );
            } else {
                return new CursorLoader(
                        CompsActivity.this,
                        Contract.Comps.CONTENT_URI,
                        PROJECTION,
                        Contract.Comps.UID + " LIKE ? OR " +
                                Contract.Comps.NAME + " LIKE ? OR " +
                                Contract.Comps.IPADDR + " LIKE ? OR " +
                                Contract.Comps.TAG + " LIKE ?",
                        new String[]{
                                mCurFilter + "%",
                                "%" + mCurFilter + "%",
                                mCurFilter + "%",
                                mCurFilter + "%"
                        },
                        null);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            adapter.swapCursor(cursor);
            if ((cursor.getCount() == 0) && (mCurFilter == null)) {
                update();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            adapter.swapCursor(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newFilter = !TextUtils.isEmpty(s.toString()) ? s.toString() : null;
                if (mCurFilter == null && newFilter == null) {
                    return;
                }
                if (mCurFilter != null && mCurFilter.equals(newFilter)) {
                    return;
                }
                mCurFilter = newFilter;
                getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView = (PullToRefreshListView) findViewById(R.id.listView);
        adapter = new SimpleCursorAdapter(this,
                R.layout.item_view,
                null,
                new String[]{Contract.Comps.NAME, Contract.Comps.UID},
                new int[]{R.id.user_name_text_view, R.id.body_text_view},
                0);

        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                update();
            }
        });
        listView.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
    }

    void update() {
        listView.setRefreshing();
        Request updateRequest = RequestFactory.getCompsRequest();
        requestManager.execute(updateRequest, requestListener);
    }

    public void populateDialog(Cursor cursor) {
        Intent myIntent;
        myIntent = new Intent(CompsActivity.this, CompView.class);
        myIntent.putExtra("Comp_ID", cursor.getInt(cursor.getColumnIndex(Contract.Comps._ID)));
        CompsActivity.this.startActivity(myIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        populateDialog(cursor);
    }
}
