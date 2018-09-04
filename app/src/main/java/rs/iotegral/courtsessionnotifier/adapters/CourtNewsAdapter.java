package rs.iotegral.courtsessionnotifier.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import rs.iotegral.courtsessionnotifier.fragments.MainActivityFragment;
import rs.iotegral.courtsessionnotifier.R;
import rs.iotegral.courtsessionnotifier.utils.Utils;

/**
 * Created by Lauda on 8/28/2018 22:07.
 */
public class CourtNewsAdapter extends CursorAdapter {


    public CourtNewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // Cache of children views for a news list item
    public static class ViewHolder {
        final TextView titleView;
      //  final TextView descriptionView;
        final TextView dateView;

        ViewHolder(View view) {
            titleView = view.findViewById(R.id.list_item_news_title);
            // descriptionView = view.findViewById(R.id.list_item_news_description);
            dateView = view.findViewById(R.id.list_item_news_date);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_court_news, parent, false);

        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder)view.getTag();

        vh.titleView.setText(cursor.getString(MainActivityFragment.COL_NEWS_TITLE));
       // vh.descriptionView.setText(cursor.getString(MainActivityFragment.COL_NEWS_DESCRIPTION));
        vh.dateView.setText(Utils.parseDate(cursor.getString(MainActivityFragment.COL_NEWS_DATE)));
    }
}
