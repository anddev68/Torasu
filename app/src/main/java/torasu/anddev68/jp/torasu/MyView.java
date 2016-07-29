package torasu.anddev68.jp.torasu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.EditText;
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
    private ArrayList<Kotei> kotei;
    private ArrayList<Kajuu> kajuu;

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
        //  固定点描画
        for(Kotei tmp : kotei){
            int x = tmp.p.x * getWidth() / LINE_X_NUM;
            int y = tmp.p.y * getHeight() / LINE_Y_NUM;
            //  三角を書く
            Point a = new Point(x,y);
            Point b = new Point(x-30,y+30);
            Point c = new Point(x+30,y+30);
            Path path = triangle(a,b,c);
            canvas.drawPath(path,koteiPaint);
            //  ローラ支持（ｘ開放）の場合は支点も
            if(tmp.x==0){
                canvas.drawLine(x-30, y+40, x+30, y+40, koteiPaint);
            }

        }
        //  荷重描画
        for(Kajuu tmp: kajuu){
            int x = tmp.p.x * getWidth() / LINE_X_NUM;
            int y = tmp.p.y * getHeight() / LINE_Y_NUM;
            int fx = tmp.x;
            int fy = tmp.y;
            int x2 = (tmp.p.x+fx) * getWidth() / LINE_X_NUM;
            int y2 = (tmp.p.y+fy) * getHeight() / LINE_Y_NUM;
            //  矢印を書く
            canvas.drawLine(x, y, x2, y2,kajuuPaint);
            if(fx==0) {
                canvas.drawLine(x2, y2, x2-30, (y2+y)/2, kajuuPaint);
                canvas.drawLine(x2, y2, x2+30, (y2+y)/2, kajuuPaint);
            }
            else if(fy==0) {
                canvas.drawLine(x2, y2, (x2+x)/2, y2-30, kajuuPaint);
                canvas.drawLine(x2, y2, (x2+x)/2, y2+30, kajuuPaint);
            }else{
                canvas.drawLine(x2, y2, x2, y2-30, kajuuPaint);
                canvas.drawLine(x2, y2, x2-30, y2, kajuuPaint);
            }
        }


        //canvas.drawText("一番左下が0,0になります",getWidth()-1000,getHeight()-50,textPaint);

    }

    /* 初期化メソッド */
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

        koteiPaint = new Paint();
        koteiPaint.setStyle(Paint.Style.STROKE);
        koteiPaint.setStrokeWidth(6);
        koteiPaint.setColor(Color.GREEN);

        kajuuPaint = new Paint();
        kajuuPaint.setStyle(Paint.Style.STROKE);
        kajuuPaint.setColor(Color.BLUE);
        kajuuPaint.setStrokeWidth(6);

        gestureDetector = new GestureDetector(getContext(),gestureListener);

        reset();
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
       data.add(String.format("%d %d %d %d",setten.size(),youso.size(), kajuu.size(), kotei.size()));

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

        //  荷重の処理
        for(Kajuu tmp : kajuu){
            int index = indexTable.get(tmp.p);
            data.add(String.format("%d %d %d",index, tmp.x, tmp.y));
        }

        //  固定点の処理
        for(Kotei tmp: kotei){
            int index = indexTable.get(tmp.p);
            data.add(String.format("%d %d %d",index, tmp.x, tmp.y));
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
    public String dumpStringWindows(){
        ArrayList<String> data = dumpStrings();
        StringBuilder sb = new StringBuilder();
        for(String s : data) {
            sb.append(s);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    /* リセット用メソッド */
    public void reset(){
        setten = new HashSet<>();
        youso = new HashSet<>();
        kotei = new ArrayList<>();
        kajuu = new ArrayList<>();
        selection = null;
    }

    /* タッチ処理用メソッド */
    private void onGestureDown(MotionEvent e){
        Log.d("Down","Down");
        int x = rawXToFieldX(e.getX());
        int y = rawYToFieldY(e.getY());
        listener.onClick(x,y);
    }
    private void onGestureScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        /* 動いた距離が一定以下なら点タッチと判定 */
        Log.d("Scroll",String.format("%f,%f",distanceX,distanceY));
    }

    /* 接点/要素/固定点/荷重を設置するメソッド */
    public void putSettenAndYouso(int x, int y){
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


    }
    private void putKotei(Point p, int x, int y){
        kotei.add(new Kotei(p,x,y));
    }
    private void putKajuu(Point p,int x,int y){
        kajuu.add(new Kajuu(p,x,y));
    }

    /* 置くものを選ぶメソッド */
    public void openKoteiChooseDialog(final int x,final int y){
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("固定条件を選択してください")
                .setPositiveButton("ピン支持(xy固定)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Point p =new Point(x,y);
                        putKotei(p,1,1);
                        invalidate();
                    }
                })
                .setNegativeButton("ローラ支持(y固定)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Point p =new Point(x,y);
                        putKotei(p,0,1);
                        invalidate();
                    }
                })
                .show();
    }
    public void openKajuuChooseDialog(final int x,final int y){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.content_kajuu_dialog,null);
        final EditText editText = (EditText) view.findViewById(R.id.editText1);
        final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("荷重を設定してください")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int fx = Integer.parseInt(editText.getText().toString());
                        int fy = Integer.parseInt(editText2.getText().toString());
                        putKajuu(new Point(x,y),fx,fy);
                    }
                })
                .show();
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

    private ArrayList<Point> hashSetToArrayList(HashSet<Point> hashMap){
        ArrayList<Point> list = new ArrayList<>();
        for(Point p : hashMap){
            list.add(p);
        }
        return list;
    }

    private Path triangle(Point a, Point b, Point c){
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.moveTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        return path;
    }


    private Paint strokePaint;
    private Paint fillPaint;
    private Paint textPaint;
    private Paint pointPaint;
    private Paint koteiPaint;
    private Paint kajuuPaint;

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

    private class Kotei{
        Point p;
        int x,y;
        public Kotei(Point p,int x,int y){
            this.x = x;
            this.y = y;
            this.p = p;
        }
    }
    private class Kajuu{
        int x,y;
        Point p;
        public Kajuu(Point p,int x,int y){
            this.p = p;
            this.x = x;
            this.y = y;
        }
    }

    public interface OnClickedGridListener{
        public void onClick(int x,int y);
    }
    private OnClickedGridListener listener;
    public void setOnClickedGridListener(OnClickedGridListener l){
        this.listener = l;
    }


}
