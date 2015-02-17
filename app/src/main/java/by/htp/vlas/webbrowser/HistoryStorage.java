package by.htp.vlas.webbrowser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by VlasEL on 08.02.2015 11:42
 */
public final class HistoryStorage implements Serializable, Cloneable {

    private static List<HistoryItem> history = new ArrayList<>();

    public static void addInHistory(String pUrl, String pTitle) {
        HistoryItem historyItem = new HistoryItem(pUrl, pTitle);
        history.add(historyItem);
    }

    public List<HistoryItem> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void setHistory(List<HistoryItem> history) {
        HistoryStorage.history = history;
    }

}
