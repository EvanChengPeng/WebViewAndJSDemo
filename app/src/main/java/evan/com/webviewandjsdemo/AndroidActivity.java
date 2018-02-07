package evan.com.webviewandjsdemo;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AndroidActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv1)
    TextView tv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
        ButterKnife.bind(this);

        //获取设置对象
        WebSettings webSettings = mWebView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        //实际情况时，Android更多的是调用远程JS代码，即将加载的JS代码路径改成url即可
        mWebView.loadUrl("file:///android_asset/Android.html");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 必须另开线程进行JS方法调用(否则无法调用)
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "参数。。。";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            //方式二
                            //优点：该方法比方式一效率更高、使用更简洁。
                            //因为该方法的执行不会使页面刷新，而方式一（loadUrl ）的执行则会。
                            //Android 4.4 后才可使用
                            mWebView.evaluateJavascript("javascript:callJS('" + msg + "')", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    //此处为 js 返回的结果
                                    tv.setText(value);
                                }
                            });
                        } else {
                            //方式一
                            // 注意调用的JS方法名要对应上
                            // 调用javascript的callJS()方法
                            mWebView.loadUrl("javascript:callJS('" + msg + "')");
                        }
                    }
                });
            }
        });

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(AndroidActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            //方式二
                            //优点：该方法比方式一效率更高、使用更简洁。
                            //因为该方法的执行不会使页面刷新，而方式一（loadUrl ）的执行则会。
                            //Android 4.4 后才可使用
                            mWebView.evaluateJavascript("javascript:changeTextColor()", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    //此处为 js 返回的结果
                                    tv1.setText(value);
                                }
                            });
                        } else {
                            //方式一
                            // 注意调用的JS方法名要对应上
                            // 调用javascript的callJS()方法
                            mWebView.loadUrl("javascript:changeTextColor()");
                        }
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }


        });
    }


}
