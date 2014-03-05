package ru.synthet.synthit.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.foxykeep.datadroid.requestmanager.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RequestFactory;
import ru.synthet.synthit.model.provider.Contract;

public class UsersActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int LOADER_ID = 1;

    private static final String[] PROJECTION = {
            Contract.Users._ID,
            Contract.Users.UID,
            Contract.Users.DN,
            Contract.Users.DISPLAY_NAME,
            Contract.Users.DISPLAY_NAME_UP,
            Contract.Users.DESCRIPTION,
            Contract.Users.PASSWORD,
            Contract.Users.PASSWORD_AD
    };

    private LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
            if (mCurFilter == null) {
                return new CursorLoader(
                        UsersActivity.this,
                        Contract.Users.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null
                );
            } else {
                return new CursorLoader(
                        UsersActivity.this,
                        Contract.Users.CONTENT_URI,
                        PROJECTION,
                        Contract.Users.UID + " LIKE ? OR " + Contract.Users.DISPLAY_NAME_UP + " LIKE ?",
                        new String[]{mCurFilter + "%", "%" + mCurFilter.toUpperCase() + "%"},
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
                new String[]{Contract.Users.UID, Contract.Users.DISPLAY_NAME},
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
        Request updateRequest = RequestFactory.getUsersRequest();
        requestManager.execute(updateRequest, requestListener);
    }

    public void populateDialog(Cursor cursor) {
        TextView textUser = (TextView) itemDialog.findViewById(R.id.textUser);
        textUser.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.DISPLAY_NAME)));
        TextView textDesc = (TextView) itemDialog.findViewById(R.id.textDesc);
        textDesc.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.DESCRIPTION)));
        TextView textPass = (TextView) itemDialog.findViewById(R.id.textPass);
        textPass.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.PASSWORD)));
        TextView textPass2 = (TextView) itemDialog.findViewById(R.id.textPass2);
        textPass2.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.PASSWORD_AD)));
    }
}
