package by.htp.vlas.webbrowser;

import java.io.Serializable;

/**
 * Created by VlasEL on 08.02.2015 16:35
 */
public class HistoryItem implements Serializable {

    public HistoryItem() {
    }

    public HistoryItem(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    String mUrl;
    String mTitle;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
