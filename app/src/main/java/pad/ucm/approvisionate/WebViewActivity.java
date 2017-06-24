package pad.ucm.approvisionate;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings= myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final String linkSrc,linkPlayStore;
        Toolbar myChildToolBar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolBar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                linkSrc= "";
                linkPlayStore="";
            } else {
                linkSrc= extras.getString("github");
                linkPlayStore=extras.getString("play");
            }
        } else {
            linkSrc= (String) savedInstanceState.getSerializable("github");
            linkPlayStore=(String) savedInstanceState.getSerializable("play");

        }

        myWebView.loadUrl("file:///android_asset/web/index.html");
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                view.evaluateJavascript("loadMsg('"+linkSrc+"','"+linkPlayStore+"')", null);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void goBack(View view){
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}


