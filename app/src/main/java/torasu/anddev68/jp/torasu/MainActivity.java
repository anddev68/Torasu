package torasu.anddev68.jp.torasu;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myView = (MyView) findViewById(R.id.myView);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  プレビューする
                openDialog();
            }
        });


    }

    /**
     * 変換したテキストの内容をプレビューする
     */
    void openDialog(){
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(myView.dumpString());
        scrollView.addView(textView);
        new AlertDialog.Builder(this)
                .setTitle("Preview")
                .setView(scrollView)
                .show();

    }

}
