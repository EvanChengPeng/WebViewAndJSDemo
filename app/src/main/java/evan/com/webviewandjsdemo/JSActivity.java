package evan.com.webviewandjsdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JSActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js);
        ButterKnife.bind(this);
        //获取设置对象
        WebSettings webSettings = mWebView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //方法一
        //优点：使用简单
        //缺点：存在严重的漏洞问题
        //js中调Android方法
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        mWebView.addJavascriptInterface(new AndroidtoJs(), "androidtoJs");//AndroidtoJS类对象映射到js的test对象

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        //实际情况时，Android更多的是调用远程JS代码，即将加载的JS代码路径改成url即可
        mWebView.loadUrl("file:///android_asset/JS.html");


        //方法二
        //优点：不存在方式1的漏洞；
        //缺点：JS获取Android方法的返回值复杂
        //如果JS想要得到Android方法的返回值，只能通过 WebView 的 loadUrl （）去执行 JS 方法把返回值传递回去
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                Uri uri = Uri.parse(url);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        Set<String> collection = uri.getQueryParameterNames();
                        StringBuilder sb = new StringBuilder();
                        for (String key : collection) {
                            sb.append(key).append(":").append(uri.getQueryParameter(key)).append("、");
                        }
                        tv.setText(sb);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        //方法三
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //警告框
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                //确认框
                return super.onJsConfirm(view, url, message, result);
            }

            // 拦截输入框(原理同方式2)
            // 参数message:代表promt（）的内容（不是url）
            // 参数result:代表输入框的返回值
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //提示框

                // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://demo?name=Evan&age=22"（同时也是约定好的需要拦截的）

                Uri uri = Uri.parse(message);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if ( uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("demo")) {

                        //
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        Set<String> collection = uri.getQueryParameterNames();
                        StringBuilder sb = new StringBuilder();
                        for (String key : collection) {
                            sb.append(key).append(":").append(uri.getQueryParameter(key)).append("、");
                        }
                        tv.setText(sb);

                        //参数result:代表消息框的返回值(输入值)
                        result.confirm("js调用了Android的方法成功啦");
                    }
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });


    }

    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void hello(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(msg.toString());
                }
            });

        }

    }
}
