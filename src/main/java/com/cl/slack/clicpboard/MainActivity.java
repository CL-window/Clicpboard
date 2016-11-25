package com.cl.slack.clicpboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.net.URISyntaxException;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ClipboardManager";
    private ClipboardManager mClipboardManager;
    private ClipData mClipData;
    private WebView mWebView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.showText);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
        mWebView.getSettings().setBuiltInZoomControls(true);//设置使支持缩放
        initClipboard();
    }

    private void initClipboard() {
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.i(TAG,"onPrimaryClipChanged...");
                handleClipChange();
            }
        });
        handleClipChange();
    }

    private void handleClipChange() {
        mWebView.setVisibility(View.GONE);
        if (mClipboardManager.hasPrimaryClip()) {
            Log.i(TAG,"hasPrimaryClip..." + mClipboardManager.getPrimaryClipDescription().toString());
            ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
            CharSequence pasteText = item.getText(); // text
            if(pasteText != null){
                Log.i(TAG,"pasteText: " + pasteText);
//                String htmlText = item.coerceToHtmlText(getApplicationContext());
                if(pasteText.toString().startsWith("http")) {
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.loadUrl(pasteText.toString());
                }else {
                    mTextView.setText(pasteText);
                }
            }else {
                Uri pasteUri = item.getUri(); // uri
                if (pasteUri != null) {
                    Log.i(TAG,"pasteUri: " + pasteUri.toString());
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.loadUrl(pasteUri.toString());
                }else {
                    Intent pasteIntent = item.getIntent();
                    if (pasteIntent != null) {
                        Log.i(TAG,"pasteIntent: " + pasteIntent.toString());
                        // ClipData.Item.coerceToText()来返回一个Intent的URI，然后通过解析URI来启动Intent
//                        String clipDataString = item.coerceToText(this.getApplicationContext()).toString();
//                        Log.i(TAG,"clipDataString: " + clipDataString);
//                        try {
//                            pasteIntent = Intent.parseUri(clipDataString, 0);
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }
                        startActivity(pasteIntent);
                    } else {
                        Log.i(TAG,"PrimaryClip data invalid ...");
                    }
                }
            }

        } else {
            Log.i(TAG,"no PrimaryClip...");
        }

    }

    private void addClipData(){
        mClipboardManager.setPrimaryClip(mClipData);
    }

    public void addTextToClipboard(View view) {
        // Label : show to the user describing this clip.
//        mClipData = ClipData.newPlainText("slack_TEXT", "hello slack ...");
        mClipData = ClipData.newPlainText("slack_TEXT", "http://www.baidu.com");
        addClipData();
    }

    public void addURIToClipboard(View view) {
        mClipData = ClipData.newUri(getContentResolver(), "slack_URI",
                Uri.parse("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&" +
                        "size=b4000_4000&sec=1480045348&di=1cda3347ec7ce94bd887494c9b5a04d0&" +
                        "src=http://fun.youth.cn/yl24xs/201611/W020161124334381471989.jpg"));
        addClipData();
    }

    public void addIntentToClipboard(View view) {
        mClipData = ClipData.newIntent("slack_INTENT", new Intent(this,TestActivity.class));
        addClipData();
    }

}
