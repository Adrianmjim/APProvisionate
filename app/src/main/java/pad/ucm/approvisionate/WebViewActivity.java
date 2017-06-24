package pad.ucm.approvisionate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings= myWebView.getSettings();
        final String linkSrc,linkPlayStore;
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
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("loadMsg('"+linkSrc+"','"+linkPlayStore+"')", null);
                } else {
                    view.loadUrl("javascript:loadMsg('"+linkSrc+"','"+linkPlayStore+"')");
                }
            }
        });
        myWebView.loadUrl("file:///android_asset/web/index.html");
    }
}


