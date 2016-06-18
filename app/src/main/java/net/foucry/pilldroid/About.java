package net.foucry.pilldroid;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

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

/*        aboutView = (WebView) findViewById(R.id.aboutHtml);

        aboutView.loadUrl("file:///android_asset/about.html");
        aboutView.clearCache(true);
        aboutView.clearHistory();
        aboutView.getSettings().setJavaScriptEnabled(true);
        aboutView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);*/

        try {
            InputStream is = getAssets().open("about.html");
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            htmlString = new String(buffer);

        } catch (IOException e) {
            throw  new RuntimeException(e);
        }

        TextView htmlTextView = (TextView)findViewById(R.id.aboutHtml);
        htmlTextView.setText(Html.fromHtml(htmlString, new ImageGetter(), null));

        Log.i("PillDroid", htmlTextView.getText().toString());
    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;
            if (source.equals("ic_launcher.png")) {
                id = R.mipmap.ic_launcher;
            } else {
                return null;
            }

            Drawable d = getResources().getDrawable(id);
//            Drawable d = ResourcesCompat.getDrawable(getResources(),id, null);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    }
}
