package ru.synthet.synthit.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.TextView;
import ru.synthet.synthit.R;
import ru.synthet.synthit.model.provider.Contract;

public class CompView extends FragmentActivity {

    private static final int LOADER_ID = 3;

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
            TextView textUser = (TextView) findViewById(R.id.textUser);
            textUser.setText(cursor.getString(cursor.getColumnIndex(Contract.Users.DISPLAY_NAME)));
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

        getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
    }

}