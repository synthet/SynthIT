package ru.synthet.synthit.ui;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RestRequestManager;
import ru.synthet.synthit.model.provider.Contract;

import java.util.ArrayList;

public abstract class BaseActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";

    protected PullToRefreshListView listView;
    protected EditText inputSearch;
    protected SimpleCursorAdapter adapter;

    protected RestRequestManager requestManager;
    protected ArrayList<Request> mRequestList;

    protected AlertDialog itemDialog;

    protected String mCurFilter;

    RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {

        @Override
        public void onRequestFinished(Request request, Bundle resultData) {
            listView.onRefreshComplete();
        }

        void showError() {
            listView.onRefreshComplete();
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.
                    setTitle(android.R.string.dialog_alert_title).
                    setMessage(getString(R.string.faled_to_load_data)).
                    create().
                    show();
        }

        @Override
        public void onRequestDataError(Request request) {
            showError();
        }

        @Override
        public void onRequestCustomError(Request request, Bundle resultData) {
            showError();
        }

        @Override
        public void onRequestConnectionError(Request request, int statusCode) {
            showError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        requestManager = RestRequestManager.from(this);

        if (savedInstanceState != null) {
            mRequestList = savedInstanceState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
        } else {
            mRequestList = new ArrayList<Request>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);

            if (requestManager.isRequestInProgress(request)) {
                requestManager.addRequestListener(requestListener, request);
                setProgressBarIndeterminateVisibility(true);
            } else {
                mRequestList.remove(request);
                i--;
                // Nothing to do if it works as the cursor is automatically updated
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestManager.removeRequestListener(requestListener);
        if (itemDialog != null) {
            if (itemDialog.isShowing())
                itemDialog.dismiss();
        }
    }

    protected abstract void populateDialog(Cursor cursor);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);

        View dialogView = getLayoutInflater().inflate(R.layout.user_dialog, null);
        itemDialog = builder.setView(dialogView).
                setTitle(cursor.getString(cursor.getColumnIndex(Contract.Users.UID))).
                setCancelable(true).
                create();
        itemDialog.show();

        populateDialog(cursor);
    }
}
