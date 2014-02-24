package ru.synthet.synthit.ui;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RequestFactory;
import ru.synthet.synthit.model.RestRequestManager;
import ru.synthet.synthit.model.provider.Contract;

public class CompsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

	final String TAG = getClass().getSimpleName();

	private PullToRefreshListView listView;
	private SimpleCursorAdapter adapter;
	
	private RestRequestManager requestManager;
	
	private static final int LOADER_ID = 1;
	private static final String[] PROJECTION = { 
		Contract.Comps._ID,
		Contract.Comps.UID,
		Contract.Comps.DN,
        Contract.Comps.DISPLAY_NAME,
        Contract.Comps.DESCRIPTION,
        Contract.Comps.PASSWORD,
        Contract.Comps.PASSWORD_AD
	};
	
	private LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
			return new CursorLoader(
				CompsActivity.this,
				Contract.Comps.CONTENT_URI,
				PROJECTION,
				null,
				null,
				null
			);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
			adapter.swapCursor(cursor);
			if (cursor.getCount() == 0) {
				update();
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			adapter.swapCursor(null);
		}
	};
	
	RequestListener requestListener = new RequestListener() {
		
		@Override
		public void onRequestFinished(Request request, Bundle resultData) {
			listView.onRefreshComplete();
		}
		
		void showError() {
			listView.onRefreshComplete();
			AlertDialog.Builder builder = new AlertDialog.Builder(CompsActivity.this);
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
		setContentView(R.layout.activity_main);
		
		listView = (PullToRefreshListView)findViewById(R.id.listView);
		adapter = new SimpleCursorAdapter(this,
			R.layout.tweet_view, 
			null, 
			new String[]{ Contract.Comps.UID, Contract.Comps.DISPLAY_NAME },
			new int[]{ R.id.user_name_text_view, R.id.body_text_view }, 
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
		
		requestManager = RestRequestManager.from(this);
	}
	
	void update() {
		listView.setRefreshing();
		Request updateRequest = RequestFactory.getCompsRequest();
		requestManager.execute(updateRequest, requestListener);
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompsActivity.this);
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);

        View dialogView = getLayoutInflater().inflate(R.layout.user_dialog, null);
        builder.setView(dialogView).
                setTitle(cursor.getString(cursor.getColumnIndex(Contract.Comps.UID))).
                setCancelable(true).
                create().
                show();

        TextView textUser = (TextView) dialogView.findViewById(R.id.textUser);
        textUser.setText(cursor.getString(cursor.getColumnIndex(Contract.Comps.DISPLAY_NAME)));
        TextView textDesc = (TextView) dialogView.findViewById(R.id.textDesc);
        textDesc.setText(cursor.getString(cursor.getColumnIndex(Contract.Comps.DESCRIPTION)));
        TextView textPass = (TextView) dialogView.findViewById(R.id.textPass);
        textPass.setText(cursor.getString(cursor.getColumnIndex(Contract.Comps.PASSWORD)));
        TextView textPass2 = (TextView) dialogView.findViewById(R.id.textPass2);
        textPass2.setText(cursor.getString(cursor.getColumnIndex(Contract.Comps.PASSWORD_AD)));
    }
}
