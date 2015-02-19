package by.htp.vlas.webbrowser;

import static by.htp.vlas.webbrowser.HistoryActivity.HISTORY_ACTIVITY_URL_REQUEST_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by _guest on 07.02.2015.
 */
public class MainActivity extends Activity {

    @InjectView(R.id.btn_back)
    Button mBtnBack;

    @InjectView(R.id.btn_forward)
    Button mBtnForward;

//    @InjectView(R.id.btn_go)
//    Button mBtnGo;

    @InjectView(R.id.address)
    EditText mAddressView;

    @InjectView(R.id.page_data)
    WebView mWebView;


    private final String TAG = "MainActivity";
    private final String URI_SCHEME_HTTP = "http://";
    private final String TEXT_ENCODING_NAME_DEF = "utf-8";
    private final String STATE_ADDRESS_KEY = "address";
    private final String PREF_NAME = "web_browser_preferences";
    private final String PREF_LAST_URL = "last_url";
    private final int HISTORY_ACTIVITY_URL_REQUEST = Math.abs("URL_REQUEST".hashCode());

    private HistoryStorage mHistoryStorage = new HistoryStorage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mBtnBack.setEnabled(false);
        mBtnForward.setEnabled(false);
        setOnKeyListenerToAddressView(mAddressView);

        webViewInit();

        boolean isExtRequest = handleExtRequestIntent(getIntent());

        if(!isExtRequest && savedInstanceState == null) {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String pUrl = preferences.getString(PREF_LAST_URL, null);
            if(pUrl != null) {
                loadUrl(pUrl);
            }
        }

    }

    private boolean handleExtRequestIntent(Intent pIntent) {
        Log.d(TAG, "\nintent = " + pIntent);
        boolean result = false;
        if (pIntent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri data = pIntent.getData();

            Log.d(TAG, "intent data = " + data);

            if (data != null) {
                loadUrl(data.toString());
                result = true;
            }
        }
        return result;
    }

    //----------------------- Save-Restore -------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
        outState.putCharSequence(STATE_ADDRESS_KEY, mAddressView.getText());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
        mAddressView.setText(savedInstanceState.getCharSequence(STATE_ADDRESS_KEY));
    }

    @Override
    protected void onDestroy() {
        if(isFinishing()) {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_LAST_URL, mWebView.getUrl());
            editor.apply();
        }
        super.onDestroy();
    }

    //-----------------------/ Save-Restore -------------------------------------------

    @OnClick(R.id.btn_go)
    void btnGoAction() {
        loadUrl(null);
    }

    @OnClick(R.id.btn_back)
    void goBack() {
        mWebView.goBack();
    }

    @OnClick(R.id.btn_forward)
    void goForward() {
        mWebView.goForward();
    }

    private void loadUrl(String pLinkAddressString) {
        String uriString = (!TextUtils.isEmpty(pLinkAddressString)) ? pLinkAddressString : mAddressView.getText().toString();
        if (TextUtils.isEmpty(uriString)) return;

        hideSoftKeyboard(mAddressView);

        uriString = validateUriString(uriString);

        mAddressView.setText(uriString);
        mWebView.loadUrl(uriString);
    }

    private String validateUriString(String pUriString) {
        if (!URLUtil.isValidUrl(pUriString)) {
            Uri uri = Uri.parse(pUriString);
            String uriScheme = uri.getScheme();
            if (TextUtils.isEmpty(uriScheme)) {
                pUriString = URI_SCHEME_HTTP + pUriString;
            }
        }
        return pUriString;
    }

    // ---------------------------- Key Events ----------------------------------
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK: {
                if (mWebView.canGoBack()) {
                    goBack();
                    return true;
                } else {
                    return super.onKeyDown(keycode, event);
                }
            }
            case KeyEvent.KEYCODE_MENU: {
                invokeHistoryActivity();
                return true;
            }
            default: {
                return super.onKeyDown(keycode, event);
            }
        }
    }

    private void setOnKeyListenerToAddressView(View view) {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        ) {
                    loadUrl(null);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private boolean hideSoftKeyboard(View pView) {
        boolean result = false;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive(pView)) {
            result = imm.hideSoftInputFromWindow(pView.getWindowToken(), 0);
        }
        return result;
    }
    // ----------------------------/ Key Events ----------------------------------

    // ---------------------------- History Activity ----------------------------------
    private void invokeHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        this.startActivityForResult(intent, HISTORY_ACTIVITY_URL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == HISTORY_ACTIVITY_URL_REQUEST && resultCode == Activity.RESULT_OK) {
            String url = data.getStringExtra(HISTORY_ACTIVITY_URL_REQUEST_KEY);
            loadUrl(url);
        }
    }
    // -----------------------------/ History Activity ---------------------------------

    //--------------- web View ------------------------------------------
    private void webViewInit() {
        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setDefaultTextEncodingName(TEXT_ENCODING_NAME_DEF);
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(true);
        webViewSettings.setDisplayZoomControls(false);
        webViewSettings.setUseWideViewPort(true);
        mWebView.setInitialScale(1);
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mAddressView.setText(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mBtnBack.setEnabled(view.canGoBack());
            mBtnForward.setEnabled(view.canGoForward());
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (!isReload) {
                mHistoryStorage.addInHistory(url, view.getTitle());
                Toast.makeText(MainActivity.this
                        , (url + "\n" + getString(R.string.msg_history_event))
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }
    //---------------------------/ web View ----------------------------

}
