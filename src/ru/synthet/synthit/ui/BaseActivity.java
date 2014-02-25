package ru.synthet.synthit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RestRequestManager;

import java.util.ArrayList;

public abstract class BaseActivity extends FragmentActivity implements AdapterView.OnItemClickListener, TextWatcher {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";

    protected PullToRefreshListView listView;
    protected EditText inputSearch;
    protected SimpleCursorAdapter adapter;

    protected RestRequestManager requestManager;
    protected ArrayList<Request> mRequestList;

    protected String mCurFilter;

    RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {

        @Override
        public void onRequestFinished(Request request, Bundle resultData) {
            listView.onRefreshComplete();
        }

        void showError() {
            listView.onRefreshComplete();
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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


}
