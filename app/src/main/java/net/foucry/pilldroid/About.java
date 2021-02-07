package net.foucry.pilldroid;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.webkit.WebView;

/**
 * Created by jacques on 12/06/16.
 */
public class About extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        WebView aboutView = findViewById(R.id.aboutHtml);

        aboutView.loadUrl("file:///android_asset/about.html");
        aboutView.clearCache(true);
        aboutView.clearHistory();
        aboutView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        aboutView.setBackgroundColor(Color.WHITE);

    }
}
