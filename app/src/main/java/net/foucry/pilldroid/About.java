package net.foucry.pilldroid;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;

/**
 * Created by jacques on 12/06/16.
 */
public class About extends AppCompatActivity{

    private final String htmlText = "<body>" +
            "<h1>À propos de " + R.string.app_name + "</h1>" +
            "<img src=\"ic_launcher.png\">" +
            "</body>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        String htmlAsString = getString(R.string.html);
        Spanned htmlAsSpanned = Html.fromHtml(htmlAsString);

        WebView webView = (WebView) findViewById(R.id.aboutHtml);
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null);
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
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    }
}
