package rs.iotegral.courtsessionnotifier.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Objects;

import rs.iotegral.courtsessionnotifier.R;
import rs.iotegral.courtsessionnotifier.adapters.CourtNewsAdapter;
import rs.iotegral.courtsessionnotifier.contracts.DatabaseContract;
import rs.iotegral.courtsessionnotifier.utils.Consts;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mListView;
    private int mPosition;
    private CourtNewsAdapter mCourtNewsAdapter;
    private static final String SELECTED_KEY = "selected_position";
    private BroadcastReceiver mBroadcastReceiver;

    private static final int COURT_NEWS_LOADER = 0;

    private static final String[] COURT_NEWS_COLUMNS = {
            DatabaseContract.NewsEntity.TABLE_NAME + "." + DatabaseContract.NewsEntity._ID,
            DatabaseContract.NewsEntity.COLUMN_NEWS_TITLE,
            DatabaseContract.NewsEntity.COLUMN_NEWS_DESCRIPTION,
            DatabaseContract.NewsEntity.COLUMN_NEWS_PUBLISHED,
    };

    public static final int COL_NEWS_TITLE = 1;
    public static final int COL_NEWS_DESCRIPTION = 2;
    public static final int COL_NEWS_DATE = 3;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.requireNonNull(intent.getAction()).equals(Consts.SYNC_COMPLETED)) {
                    updateFragment();
                }
            }
        };

        IntentFilter filterRefreshUpdate = new IntentFilter();
        filterRefreshUpdate.addAction(Consts.SYNC_STARTED);
        filterRefreshUpdate.addAction(Consts.SYNC_COMPLETED);
        Objects.requireNonNull(getActivity()).registerReceiver(mBroadcastReceiver, filterRefreshUpdate);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCourtNewsAdapter = new CourtNewsAdapter(getContext(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = rootView.findViewById(R.id.listview_court_news);
        mListView.setAdapter(mCourtNewsAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri newsUri  = DatabaseContract.NewsEntity.buildNewsData();

        return new CursorLoader(Objects.requireNonNull(getActivity()),
                newsUri,
                COURT_NEWS_COLUMNS,
                null,
                null,
                DatabaseContract.NewsEntity.COLUMN_NEWS_PUBLISHED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCourtNewsAdapter.swapCursor(data);

        if (mPosition != ListView.INVALID_POSITION)
            mListView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCourtNewsAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(COURT_NEWS_LOADER, null, this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    private void updateFragment() {
        getLoaderManager().restartLoader(COURT_NEWS_LOADER, null, this);
    }
}
