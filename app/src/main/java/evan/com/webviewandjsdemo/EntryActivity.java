package evan.com.webviewandjsdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EntryActivity extends AppCompatActivity {

    @BindView(R.id.one)
    Button one;
    @BindView(R.id.two)
    Button two;
    @BindView(R.id.three)
    Button three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.one, R.id.two, R.id.three})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.one:
                intent.setClass(EntryActivity.this, AndroidActivity.class);
                break;
            case R.id.two:
                intent.setClass(EntryActivity.this, JSActivity.class);
                break;
            case R.id.three:
                intent.setAction(Intent.ACTION_VIEW);
                Uri content_url = Uri.parse("https://www.baidu.com/");
                intent.setData(content_url);
                break;
        }
        startActivity(intent);
    }

}
