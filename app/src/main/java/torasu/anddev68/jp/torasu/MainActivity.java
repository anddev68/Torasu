package torasu.anddev68.jp.torasu;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity{

    private MyView myView;
    private PutMode putMode;
    private enum PutMode{
        SETTEN_YOUSO,KOTEI,KAJUU
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  モードを切り替える
        putMode = PutMode.SETTEN_YOUSO;
        setTitle("＜接点・要素設置モード＞");

        myView = (MyView) findViewById(R.id.myView);


        //  グリッドが押されたときに呼ばれるリスナー
        myView.setOnClickedGridListener(new MyView.OnClickedGridListener() {
            @Override
            public void onClick(int x, int y) {
                //  状態によって処理を振り分ける
                switch(putMode){
                    case SETTEN_YOUSO:
                        myView.putSettenAndYouso(x,y);
                        break;
                    case KOTEI:
                        myView.openKoteiChooseDialog(x,y);
                        break;
                    case KAJUU:
                        myView.openKajuuChooseDialog(x,y);
                        break;
                }
                myView.invalidate();
            }
        });



        //  ダイアログにテキスト化したものを保存する
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        //  テキスト化したデータを保存する
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
            }
        });

        //  モードを切り替える
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMode();
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
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/torasu");
            dir.mkdirs();
            File file = new File(dir, "torasu.txt");
            FileOutputStream stream = new FileOutputStream(file);
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

    /**
     * 場をリセットする
     */
    void reset(){
        myView.reset();
        myView.invalidate();
    }

    /**
     *  モード切り替え
     */
    void changeMode(){
        switch(putMode){
            case SETTEN_YOUSO:
                putMode = PutMode.KOTEI;
                setTitle("＜固定点設置モード＞");
                break;
            case KOTEI:
                putMode = PutMode.KAJUU;
                setTitle("＜荷重設置モード＞");
                break;
            case KAJUU:
                putMode = PutMode.SETTEN_YOUSO;
                setTitle("＜接点・要素設置モード＞");
                break;
        }
    }



}
