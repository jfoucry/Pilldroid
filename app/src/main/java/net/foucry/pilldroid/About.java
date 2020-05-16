package net.foucry.pilldroid;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.webkit.WebView;

/**
 * Created by jacques on 12/06/16.
 */
public class About extends AppCompatActivity{

    private WebView aboutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        String htmlString = null;

        aboutView = (WebView) findViewById(R.id.aboutHtml);

        aboutView.loadUrl("file:///android_asset/about.html");
        aboutView.clearCache(true);
        aboutView.clearHistory();
        aboutView.getSettings().setJavaScriptEnabled(true);
        aboutView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        aboutView.setBackgroundColor(Color.WHITE);


    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;
            if (source.equals("ic_launcher.png")) {
                id = R.mipmap.ic_launcher;
            } else {
                return null;
            }

//            Drawable d = getResources().getDrawable(id);
            //Drawable d = ResourcesCompatApi21.getDrawable(getResources(),id, null);
            Drawable d = ContextCompat.getDrawable(getApplicationContext(),id);
            assert d != null;
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    }
}
