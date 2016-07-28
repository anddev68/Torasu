package torasu.anddev68.jp.torasu;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
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

    /**
     * テキストを保存する
     */
    void saveToFile(){
        try {
            FileOutputStream stream = openFileOutput("torasu.txt",MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(myView.dumpString());
            writer.close();
            Toast.makeText(this,"torasu.txt was saved!",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"ファイル保存に失敗しました",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
