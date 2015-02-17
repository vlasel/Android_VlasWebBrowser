package by.htp.vlas.webbrowser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by VlasEL on 08.02.2015 16:17
 */
public class HistoryAdapter extends BaseAdapter {

    private List<HistoryItem> mHistory;

    public HistoryAdapter(List<HistoryItem> history) {
        mHistory = history;
    }

    @Override
    public int getCount() {
        return mHistory.size();
    }

    @Override
    public HistoryItem getItem(int position) {
        return mHistory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        if (convertView == null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_history_item, parent, false);
        } else {
            itemView = convertView;
        }

        ((TextView) itemView.findViewById(R.id.history_title)).setText(getItem(position).getTitle());
        ((TextView) itemView.findViewById(R.id.history_url)).setText(getItem(position).getUrl());

        return itemView;
    }
}
