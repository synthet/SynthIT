package ru.synthet.synthit.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.RequestFactory;
import ru.synthet.synthit.model.provider.Contract;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.AlertDialog;
import android.database.Cursor;

public class UsersActivity extends BaseActivity implements AdapterView.OnItemClickListener, TextWatcher {

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
            }  else {
                return new CursorLoader(
                    UsersActivity.this,
                    Contract.Users.CONTENT_URI,
                    PROJECTION,
                    Contract.Users.UID + " LIKE ? OR " + Contract.Users.DISPLAY_NAME_UP + " LIKE ?",
                    new String[] { mCurFilter+"%", "%"+mCurFilter.toUpperCase()+"%" },
                    null);
            }
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(this);

		listView = (PullToRefreshListView)findViewById(R.id.listView);
		adapter = new SimpleCursorAdapter(this,
			R.layout.tweet_view, 
			null, 
			new String[]{ Contract.Users.UID, Contract.Users.DISPLAY_NAME },
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
    }

	void update() {
        listView.setRefreshing();
        Request updateRequest = RequestFactory.getUsersRequest();
        requestManager.execute(updateRequest, requestListener);
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);

        View dialogView = getLayoutInflater().inflate(R.layout.user_dialog, null);
        builder.setView(dialogView).
                setTitle(cursor.getString(cursor.getColumnIndex(Contract.Users.UID))).
                setCancelable(true).
                create().
                show();

        TextView textUser = (TextView) dialogView.findViewById(R.id.textUser);
        textUser.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.DISPLAY_NAME)));
        TextView textDesc = (TextView) dialogView.findViewById(R.id.textDesc);
        textDesc.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.DESCRIPTION)));
        TextView textPass = (TextView) dialogView.findViewById(R.id.textPass);
        textPass.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.PASSWORD)));
        TextView textPass2 = (TextView) dialogView.findViewById(R.id.textPass2);
        textPass2.setText(cursor.getString(cursor.getColumnIndex(Contract.Comps.PASSWORD_AD)));
    }

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
}
