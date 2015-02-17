package by.htp.vlas.webbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by VlasEL on 08.02.2015 16:11
 */
public class HistoryActivity extends Activity {

    private HistoryStorage mHistoryStorage = new HistoryStorage();

    public final static String HISTORY_ACTIVITY_URL_REQUEST_KEY = "URL_REQUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.inject(this);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new HistoryAdapter(mHistoryStorage.getHistory()));
    }

    @OnItemClick(android.R.id.list)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HistoryItem historyItem = (HistoryItem) parent.getItemAtPosition(position);

        Intent intent = new Intent();
        intent.putExtra(HISTORY_ACTIVITY_URL_REQUEST_KEY, historyItem.getUrl());

        setResult(RESULT_OK, intent);
        finish();
    }


}
