package in.alertmeu.a4b.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.alertmeu.a4b.R;

public class ShowNotificationActivity extends AppCompatActivity {
    WebView webView;
    String url = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);
        Intent intent = getIntent();
        webView = (WebView) findViewById(R.id.webView1);
        progressDialog = ProgressDialog.show(ShowNotificationActivity.this, "", "Loading...", true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(intent.getStringExtra("url")); // Here You can put your Url
        webView.setWebChromeClient(new WebChromeClient() {
        });

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
                //Toast.makeText(context, "Page Load Finished", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(ShowNotificationActivity.this, HomePageActivity.class);
        startActivity(setIntent);
        finish();
    }
}