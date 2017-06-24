package pad.ucm.approvisionate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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

            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if(url!=null && (url.startsWith("http://")||url.startsWith("https://"))){
                    myWebView.loadUrl(url);
                    return true;
                }
                else{
                    Context c=getApplicationContext();
                    int d= Toast.LENGTH_SHORT;
                    Toast toast=Toast.makeText(c,url,d);
                    toast.show();
                    return false;
                }

            }
        });
        myWebView.loadUrl("file:///android_asset/web/index.html");
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


