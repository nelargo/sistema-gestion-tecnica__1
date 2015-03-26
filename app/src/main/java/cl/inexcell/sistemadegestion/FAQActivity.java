package cl.inexcell.sistemadegestion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Felipe on 05/03/2015.
 */
public class FAQActivity extends Activity {
    WebView faq;
    String FAQ_URL = "https://pcba.telefonicachile.cl/smartphone/doc/index.html";
    Activity activity;
    ProgressBar progressBar;

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity sin parte superior
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_faq);
        activity = this;
        progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        faq = (WebView)this.findViewById(R.id.faq_webview);

        faq.getSettings().setJavaScriptEnabled(true);
        faq.setWebViewClient(new MyWebViewClient());
        faq.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int progress)
            {
                // update the progressBar
                progressBar.setProgress(progress);
            }

        });
        faq.loadUrl(FAQ_URL);
        faq.requestFocus();
    }

    public void prev_page(View v){
        faq.goBack();
    }

    public void home_page(View v){
        faq.loadUrl(FAQ_URL);
        faq.requestFocus();
    }

    public void reload_page(View v){
        faq.reload();
    }


    public void back(View v){finish();}
    public void shutdown(View v){
        Principal.p.finish();
        finish();
    }
}
