package torasu.anddev68.jp.torasu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2016/07/11.
 */
public class MyView extends View {


    private HashSet<Point> setten;
    private HashSet<Pair<Point,Point>> youso;
    private Point selection;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //  グリッド描画
        for(int x=0; x<getWidth(); x+=getWidth()/LINE_X_NUM) {
            canvas.drawLine(x, 0, x, getHeight(), strokePaint);
        }
        for(int y=0; y<getHeight(); y+=getHeight()/LINE_Y_NUM) {
            canvas.drawLine(0, y, getWidth(), y, strokePaint);
        }
        //  点描画
        Iterator<Point> it = setten.iterator();
        for(int i=0; it.hasNext(); i++){
            Point p = it.next();
            int x = p.x * getWidth() / LINE_X_NUM;
            int y = p.y * getHeight() / LINE_Y_NUM;
            if( p.equals(selection)) {
                canvas.drawCircle(x, y, 10, redPaint);
            }else{
                canvas.drawCircle(x,y,10,blackPaint);
            }
            canvas.drawText(""+i, x-10, y-10, blackPaint);
        }
        //  線描画
        Iterator<Pair<Point,Point>>  yousoIt = youso.iterator();
        for(int i=0; yousoIt.hasNext(); i++){
            Pair<Point,Point> pair = yousoIt.next();
            System.out.println(pair);
            int x1 = pair.first.x * getWidth() / LINE_X_NUM;
            int y1 = pair.first.y * getHeight() / LINE_Y_NUM;
            int x2 = pair.second.x * getWidth() / LINE_X_NUM;
            int y2 = pair.second.y * getHeight() / LINE_Y_NUM;
            canvas.drawLine(x1,y1,x2,y2,redPaint);
            int x = (x1+x2)/2;
            int y = (y1+y2)/2;
            canvas.drawText(""+i, x, y, redPaint);

        }

        //canvas.drawText("一番左下が0,0になります",getWidth()-1000,getHeight()-50,textPaint);

    }

    private void init(){
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(1);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        fillPaint = new Paint();
        fillPaint.setStrokeWidth(1);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.RED);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);
        pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(Color.BLACK);

        redPaint = new Paint();
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(3);
        redPaint.setTextSize(30);

        blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(3);
        blackPaint.setTextSize(30);

        gestureDetector = new GestureDetector(getContext(),gestureListener);

        setten = new HashSet<>();
        youso = new HashSet<>();
    }

    /* 現在の状態をテキストに吐き出す */
    public ArrayList<String> dumpStrings(){
        ArrayList<String> data = new ArrayList<String>();

        //  最も左と下を0,0に合わせるための処理
        int offsetX = 0;
        int offsetY = 0;
        Iterator<Point> settenIt = setten.iterator();
        while(settenIt.hasNext()){
            Point p = settenIt.next();
            offsetX = Math.max(offsetX,p.x);
            offsetY = Math.max(offsetY,p.y);
        }

        //  接点 →番号変換表の作成
        HashMap<Point,Integer> indexTable = new HashMap<>();
        settenIt = setten.iterator();
        for(int i=0; settenIt.hasNext(); i++){
            indexTable.put(settenIt.next(),i);
        }

        //   ヘッダーを書き出す
       data.add(String.format("%d %d 0 0",setten.size(),youso.size()));

        //  接点を書き出す
        settenIt = setten.iterator();
        while(settenIt.hasNext()){
            Point p = settenIt.next();
            int x = offsetX -p.x;
            int y = offsetY - p.y;
            data.add(String.format("%d %d",x,y));
        }

        //  要素を書き出す
        Iterator<Pair<Point,Point>> yousoIt = youso.iterator();
        while(yousoIt.hasNext()){
            Pair<Point,Point> pair = yousoIt.next();
            int idx1 = indexTable.get(pair.first);
            int idx2 = indexTable.get(pair.second);
            data.add(String.format("%d %d %d %d", idx1, idx2, 1, 1));
        }

        return data;
    }
    public String dumpString(){
        ArrayList<String> data = dumpStrings();
        StringBuilder sb = new StringBuilder();
        for(String s : data) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }


    /* タッチ処理用メソッド */
    private void onGestureDown(MotionEvent e){
        Log.d("Down","Down");
        int x = rawXToFieldX(e.getX());
        int y = rawYToFieldY(e.getY());
        Point p =new Point(x,y);
        if( setten.contains(p)){   //  既に置いてある接点の場合
            if( selection!= null){  //  1点が選択済みなら
                if( getDistance(p,selection) == 1){ //  距離が1であるなら
                    youso.add(new Pair(p,selection));   //  線を引く
                    selection = null;
                }else{
                    Toast.makeText(getContext(),"距離が0または2以上です",Toast.LENGTH_SHORT).show();
                }
            }else{
                selection = p;
            }
        }else{
            /* 任意の点にデータを追加 */
            setten.add(p);
        }




        invalidate();


    }
    private void onGestureScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        /* 動いた距離が一定以下なら点タッチと判定 */
        Log.d("Scroll",String.format("%f,%f",distanceX,distanceY));
    }



    /* 座標返還用メソッド */
    private int rawXToFieldX(float x){
        int fx = (int)( x * LINE_X_NUM / getWidth() + 0.5);
        return fx;
    }
    private int rawYToFieldY(float y){
        int fy = (int)( y  *LINE_Y_NUM / getHeight() + 0.5);
        return fy;
    }

    /**
     *  距離を返す
     *  x,yのどちらか長いほう
     *  斜めは考慮しない
     **/
    private int getDistance(Point p1,Point p2){
        return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
    }




    private Paint strokePaint;
    private Paint fillPaint;
    private Paint textPaint;
    private Paint pointPaint;

    private Paint redPaint;
    private Paint blackPaint;

    private final int LINE_X_NUM = 6;
    private final int LINE_Y_NUM = 9;


    private GestureDetector gestureDetector;
    private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener(){
        @Override
        public boolean onDown(MotionEvent e){
            onGestureDown(e);
            return super.onDown(e);
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            onGestureScroll(e1,e2,distanceX,distanceY);
            return super.onScroll(e1,e2,distanceX,distanceY);
        }
    };
}
