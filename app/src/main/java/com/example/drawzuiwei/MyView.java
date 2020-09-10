package com.example.drawzuiwei;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.drawzuiwei.bean.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyView extends View {

    private static final int UPDATA = 100;
    private  Context mContext;//上下文
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA:
                    Log.e("handleMessage", "更新");
                    smoothZoomOut();
                    break;
            }
        }
    };

    //画笔
    private Paint mZhuoWeiNumPaint;//桌位号的画笔
    private Paint mZhuoWeiPaint;//桌位画笔

    //画笔颜色
    private int mZhuoWeiWhiteColor = Color.parseColor("#ffffff");//白色;
    private int mZhuoWeiGrayColor = Color.parseColor("#778899");//灰色;

    boolean clickState = false;// 点击状态 false:结束 true:正在进行中

    //桌位文字大小
    private int mZhuoWeiFontSize = 12;

    private int touchState;
    private int STATE_TWO_POINT = 100;//两个手指
    private int STATE_ONE_POINT = 200;//一个手指

    private float originalWidthHeight = 60; // 图片宽高
    private float widthHeight = 60; // 图片宽高

    int SCREEN_WIDTH = 715;// 电脑屏幕的宽度
    int SCREEN_HEIGHT = 600;// 电脑屏幕的高度

    float clickXTemp;//点击的X的位置
    float clickYTemp;//点击的Y的位置

    int PARAM_RATE = 3; // 最终放大的图像是原始尺寸的几倍大小
    float PARAM_ZOOM_OUT_IN = 1.05f; //图像放大的参数

    //宽度
    WidthHeight textWHSmall_1;//1对应的宽高
    WidthHeight textWHSmall_2;//2对应的宽高
    WidthHeight textWHSmall_3;//3对应的宽高

    Bitmap reserve_4_bukexuan;
    Bitmap reserve_6_bukexuan;
    Bitmap reserve_8_bukexuan;
    Bitmap reserve_10_bukexuan;
    Bitmap reserve_10_baojian_bukexuan;
    Bitmap reserve_12_bukexuan;
    Bitmap reserve_12_lianzhuo_bukexuan;
    Bitmap reserve_12_baojian_bukexuan;
    Bitmap reserve_12_lianzhuo_baojian_bukexuan;

    Bitmap reserve_4_kexuan;
    Bitmap reserve_6_kexuan;
    Bitmap reserve_8_kexuan;
    Bitmap reserve_10_kexuan;
    Bitmap reserve_10_baojian_kexuan;
    Bitmap reserve_12_kexuan;
    Bitmap reserve_12_lianzhuo_kexuan;
    Bitmap reserve_12_baojian_kexuan;
    Bitmap reserve_12_lianzhuo_baojian_kexuan;


    Bitmap reserve_4_yixuan;
    Bitmap reserve_6_yixuan;
    Bitmap reserve_8_yixuan;
    Bitmap reserve_10_yixuan;
    Bitmap reserve_10_baojian_yixuan;
    Bitmap reserve_12_yixuan;
    Bitmap reserve_12_lianzhuo_yixuan;
    Bitmap reserve_12_baojian_yixuan;
    Bitmap reserve_12_lianzhuo_baojian_yixuan;

    private static final String TAG = MyView.class.getSimpleName();
    private Paint linePaint;//画笔


    private ArrayList<Point> list;
    private int viewWidth;//View的宽度
    private int viewHeight;//View的高度
//    private Bitmap bitmap;
    private Bitmap bitmapRed;
    private GestureDetector mGestureDetector;

    private int bitmapWidth;
    private int bitmapHeight;

    //移动过程中的参数
    private double beforeDistance = 0;
    private double nowDistance = 0;
    private float downX;//起始点的X
    private float downY;//起始点的Y
    private int containerWidth;//容器的宽
    private int containerHeight;//容器的高
    private float beforePointOneX;
    private float beforePointOneY;
    private float beforePointTwoX;
    private float beforePointTwoY;
    private float nowPointOneX;
    private float nowPointOneY;
    private float nowPointTwoX;
    private float nowPointTwoY;
    private Map<String, Bitmap> bitmapMap;//存储图像的Bitmap


    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 手势监听器
        MySimpleOnGestureListener myGestureDetectore = new MySimpleOnGestureListener();
        mGestureDetector = new GestureDetector(myGestureDetectore);
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(1);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewWidth = getWidth();
        viewHeight = getHeight();
        Log.e(TAG, "viewWidth:" + viewWidth + ",viewHeight:" + viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 将内容绘制到缓存画布上
        for (int i = 0; i < this.list.size(); i++) {
            Point item = list.get(i);
            float x = item.x;
            float y = item.y;

            // 绘制对应的桌位
            Bitmap bitmap = null;
            float width = this.widthHeight;
            float height = this.widthHeight;
            //reserve_4_0_stat0_1000
            String key = "reserve" + "_" + item.capacity + "_" + item.table_type  + "_" + "state" + item.status + "_" + item.position;
            Bitmap bitmapTemp = bitmapMap.get(key);
            bitmap = big(bitmapTemp, width, height);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, x, y, mZhuoWeiPaint);
            }

            float rateTemp = this.widthHeight / this.originalWidthHeight;
            Log.e("比例:", "rateTemp:" + rateTemp);

            // 绘制桌号
            String tableNo = item.table_no;// 桌号
            float textWidth = 0;// 桌号的宽度
            float textHeight = 0;// 桌号的高度
            if ((tableNo + "").length() == 1) {
                textWidth = this.textWHSmall_1.width * rateTemp;
                textHeight = this.textWHSmall_1.height * rateTemp;
            } else if ((tableNo + "").length() == 2) {
                textWidth = this.textWHSmall_2.width * rateTemp;
                textHeight = this.textWHSmall_2.height * rateTemp;
            } else if ((tableNo + "").length() == 3) {
                textWidth = this.textWHSmall_3.width * rateTemp;
                textHeight = this.textWHSmall_3.height * rateTemp;
            }

            float centerX = x + width / 2;
            float centerY = y + height / 2;

            mZhuoWeiNumPaint.setTextSize(mZhuoWeiFontSize * rateTemp);
            canvas.drawText(tableNo, centerX - textWidth / 2, centerY + textHeight / 2, mZhuoWeiNumPaint);
        }
    }

    /**
     * 把传进来的bitmap对象转换为宽度为x,长度为y的bitmap对象
     */
    public static Bitmap big(Bitmap b, float x, float y) {
        if (b != null){
            int w = b.getWidth();
            int h = b.getHeight();
            float sx = (float) x / w;
            float sy = (float) y / h;
            Matrix matrix = new Matrix();
            matrix.postScale(sx, sy); // 长和宽放大缩小的比例
            Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
            return resizeBmp;
        } else {
            return null;
        }
    }

    private void drawTest(Canvas canvas) {
        canvas.drawLine(0, 50, viewWidth, 50, linePaint);
        canvas.drawLine(50, 0, 50, viewHeight, linePaint);
        canvas.drawLine(100, 0, 100, viewHeight, linePaint);
        canvas.drawLine(0, 100, viewWidth, 100, linePaint);

    }

    public void setData(ArrayList<Point> list) {
        this.list = list;
        // 初始化参数
        initParams();
        // 绘制
        //invalidate();
    }

    private void initParams() {
        // 获得容器的宽高
        containerWidth = getWidth();
        containerHeight = getHeight();

        // 设置画笔
        initPaint();

        // 分别测量当内容为 1，11，111时,文字的宽高
        textWHSmall_1 = measureTextWidthHeight("1");
        textWHSmall_2 = measureTextWidthHeight("11");
        textWHSmall_3 = measureTextWidthHeight("111");

        // 缓存图片
        loadImg();

        // 计算座位在页面上的位置（位置颠倒）
        for (int i = 0; i < this.list.size(); i++) {
            Point item = list.get(i);
            item.x = item.ypoint / SCREEN_HEIGHT * this.containerWidth;  // 在y轴占的比例 * 容器的宽度
            item.y = item.xpoint / SCREEN_WIDTH * this.containerHeight;// 在x轴占的比例 * 容器的高度
        }
    }

    /**
     * 根据资源的名字获取它的ID
     *
     * @param name 要获取的资源的名字 资源的类型，如drawable, string 。。。
     * @return 资源的id
     */
    public int getDrawableResId(String name) {
        String packageName = mContext.getApplicationInfo().packageName;
        return getResources().getIdentifier(name, "drawable", packageName);
    }



    /**
     * 缓存图片
     */
    private void loadImg() {
        bitmapMap = new HashMap<>();
        //reserve_4_0_stat0_1000
        String[] name = {"reserve_4","reserve_6","reserve_8","reserve_10","reserve_12"};
        String[] checkState = {"state1","state2","state3"}; //1:已选 2：可选 3：不可选
        String[] positionState = {"1000","1001","1002","1003"};
        long start = System.currentTimeMillis();
        // 包间
        for (int i = 0;i < 2; i++){
            for (int j = 0; j < name.length; j++) {
                for (int k = 0; k < checkState.length; k++) {
                    for (int l = 0; l < positionState.length; l++) {
                        String key = name[j] + "_" + i + "_" + checkState[k] + "_" + positionState[l];
                        bitmapMap.put(key,BitmapFactory.decodeResource(getResources(), getDrawableResId(key)));
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        Log.e("timetime",(end - start)+"");


//        //不可选的桌位
//        reserve_4_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_4_bukexuan);
//        reserve_6_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_6_bukexuan);
//        reserve_8_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_8_bukexuan);
//        reserve_10_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_bukexuan);
//        reserve_10_baojian_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_baojian_bukexuan);
//        reserve_12_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_bukexuan);
//        reserve_12_lianzhuo_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_bukexuan);
//        reserve_12_baojian_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_baojian_bukexuan);
//        reserve_12_lianzhuo_baojian_bukexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_baojian_bukexuan);
//        //可选的桌位
//        reserve_4_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_4_kexuan);
//        reserve_6_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_6_kexuan);
//        reserve_8_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_8_kexuan);
//        reserve_10_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_kexuan);
//        reserve_10_baojian_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_baojian_kexuan);
//        reserve_12_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_kexuan);
//        reserve_12_lianzhuo_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_kexuan);
//        reserve_12_baojian_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_baojian_kexuan);
//        reserve_12_lianzhuo_baojian_kexuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_baojian_kexuan);
//        //已选的桌位
//        reserve_4_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_4_yixuan);
//        reserve_6_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_6_yixuan);
//        reserve_8_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_8_yixuan);
//        reserve_10_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_yixuan);
//        reserve_10_baojian_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_10_baojian_yixuan);
//        reserve_12_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_yixuan);
//        reserve_12_lianzhuo_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_yixuan);
//        reserve_12_baojian_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_baojian_yixuan);
//        reserve_12_lianzhuo_baojian_yixuan = BitmapFactory.decodeResource(getResources(), R.drawable.reserve_12_lianzhuo_baojian_yixuan);
    }

    class WidthHeight {
        float width;
        float height;

        public WidthHeight(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }

    private WidthHeight measureTextWidthHeight(String content) {
        float width = mZhuoWeiNumPaint.measureText(content);
        float height = measureTextHeight(mZhuoWeiNumPaint);
        return new WidthHeight(width, height);
    }

    // 初始化画笔
    private void initPaint() {
        // 桌号画笔
        mZhuoWeiNumPaint = new Paint();
        mZhuoWeiNumPaint.setAntiAlias(true);
        mZhuoWeiNumPaint.setColor(mZhuoWeiWhiteColor);
        mZhuoWeiNumPaint.setTextSize(mZhuoWeiFontSize);
        // 桌位画笔(绘制bitmap不需要设置)
        mZhuoWeiPaint = new Paint();
    }

    /**
     * 测量文字的高度
     * --经测试后发现，采用另一种带Rect的方式，获得的数据并不准确。
     * 特别是在一些对文字有一些倾斜处理的时候
     *
     * @param paint
     * @return
     */
    public static float measureTextHeight(Paint paint) {
        float height = 0f;
        if (null == paint) {
            return height;
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        height = fontMetrics.descent - fontMetrics.ascent;
        return height;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        int pointCount = event.getPointerCount();
        int n = event.getAction();
        Log.e(TAG, "nCnt:" + pointCount);
        //ACTION_MASK:表示多点触控
        if (pointCount == 2) {
            touchState = STATE_TWO_POINT;
            //两个手指移动
            Log.e("testtest", "两个手指按下");
            // 按下
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                beforePointOneX = event.getX(0);
                beforePointOneY = event.getY(0);
                beforePointTwoX = event.getX(1);
                beforePointTwoY = event.getY(1);
            } else {
                nowPointOneX = event.getX(0);
                nowPointOneY = event.getY(0);
                nowPointTwoX = event.getX(1);
                nowPointTwoY = event.getY(1);
                beforeDistance = getDistance(beforePointOneX, beforePointOneY, beforePointTwoX, beforePointTwoY);
                nowDistance = getDistance(nowPointOneX, nowPointOneY, nowPointTwoX, nowPointTwoY);
                // 不能继续放大条件:当图片的宽高 > 3倍的原始图片宽高 并且 当前的状态是放大的状态
                if (this.widthHeight >= this.originalWidthHeight * PARAM_RATE && this.nowDistance > this.beforeDistance) {
                    return true;
                }
                // 不能在继续缩小的条件：当图片的宽高 < 原始图片的宽高 并且 此时是一种缩小的状态
                if (this.widthHeight <= this.originalWidthHeight && this.nowDistance < this.beforeDistance) {
                    return true;
                }
                // 计算变化的比例
                float rate = (float) (1 - (1 - this.nowDistance / this.beforeDistance) / 2); // /2 是为了将当前变化的效率降低
                // 计算图片放大的宽度
                this.widthHeight = this.widthHeight * rate;
                // 计算两个手指中心点的坐标
                float centerX = (event.getX(0) + event.getX(1)) / 2;
                float centerY = (event.getY(0) + event.getY(1)) / 2;
                // 根据中心点的坐标 + 距离比例来重新计算坐标
                for (int i = 0; i < this.list.size(); i++) {
                    Point item = this.list.get(i);
                    // 计算中心点和item的距离
                    float dx = centerX - item.x;
                    float dy = centerY - item.y;
                    float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    // 计算中心点和item的角度
                    float sinValue = Math.abs(dy / distance);
                    float cosValue = Math.abs(dx / distance);
                    // 计算扩大后的距离
                    float longDistance = distance * rate;
                    if (dx == 0 && dy == 0) { // 说明是同一个点
                    } else if (dx > 0 && dy <= 0) { // 中心点在第一象限
                        item.x = centerX - longDistance * cosValue;
                        item.y = centerY + longDistance * sinValue;
                    } else if (dx <= 0 && dy <= 0) { // 中心点在第二象限
                        item.x = centerX + longDistance * cosValue;
                        item.y = centerY + longDistance * sinValue;
                    } else if (dx < 0 && dy > 0) { // 中心点在第三象限
                        item.x = centerX + longDistance * cosValue;
                        item.y = centerY - longDistance * sinValue;
                    } else if (dx >= 0 && dy > 0) { // 中心点在第四象限
                        item.x = centerX - longDistance * cosValue;
                        item.y = centerY - longDistance * sinValue;
                    }
                }
                invalidate();
                // 保存上一时刻的信息
                beforePointOneX = nowPointOneX;
                beforePointOneY = nowPointOneY;
                beforePointTwoX = nowPointTwoX;
                beforePointTwoY = nowPointTwoY;
            }
        }
        else {
            //单个手指
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX = event.getX();
                downY = event.getY();
                this.touchState = STATE_ONE_POINT;
                Log.e(TAG, "downX:" + downX + "downY:" + downY);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if(touchState == STATE_ONE_POINT){
                    float x = (event.getX() - downX);
                    float y = (event.getY() - downY);
                    // 计算数据里面的x值的最大值和最小值和y值对应的最大值和最小值
                    float minX = 0;
                    float maxX = 0;
                    float minY = 0;
                    float maxY = 0;
                    for (int i = 0; i < list.size(); i++) {
                        Point point = list.get(i);
                        if (i == 0){
                            minX = point.x;
                            minY = point.y;
                            maxX = point.x;
                            maxY = point.y;
                        }
                        if (point.x > maxX){
                            maxX = point.x;
                        }
                        if (point.y > maxY){
                            maxY = point.y;
                        }
                        if (point.x < minX){
                            minX = point.x;
                        }
                        if (point.y < minY){
                            minY = point.y;
                        }
                    }
                    if (x > 0 && minX > 50){//向右
                        Log.e("testtest","1111");
                    } else if (x < 0 && maxX < containerWidth - widthHeight -50){//向左
                        Log.e("testtest","22222");
                    } else {
                        Log.e("testtest","666");
                        //重新计算位置
                        for (int i = 0; i < list.size(); i++) {
                            Point point = list.get(i);
                            point.x = point.x + x;
                        }
                    }

                    if (y > 0 && minY > 50){//向下
                        Log.e("testtest","444");
                    } else if (y < 0 && maxY < containerHeight - widthHeight - 50){//向上
                        Log.e("testtest","5555");
                    } else {
                        Log.e("testtest","666");
                        //重新计算位置
                        for (int i = 0; i < list.size(); i++) {
                            Point point = list.get(i);
                            //point.x = point.x + x;
                            point.y = point.y + y;
                        }
                    }
                    //重新计算位置
//                    for (int i = 0; i < list.size(); i++) {
//                        Point point = list.get(i);
//                        point.x = point.x + x;
//                        point.y = point.y + y;
//                    }
                    downX = event.getX();
                    downY = event.getY();
                }
            }
        }
        invalidate();
        return true;
    }

    // 计算两个点的距离
    public float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt((x * x) + (y * y));
    }

    private void calculation(float x, float y) {
        for (int i = 0; i < list.size(); i++) {
            Point point = list.get(i);
            point.x = point.x + x;
            point.y = point.y + y;
        }
    }

    public void add() {
//        float key = (float) (10.0 / 9);
//        //bitmap的比例缩小
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * key), (int) (height * key), false);
//        bitmapRed = Bitmap.createScaledBitmap(bitmapRed, (int) (width * key), (int) (height * key), false);
//        bitmapWidth = bitmap.getWidth();
//        bitmapHeight = bitmap.getHeight();
//        // 计算坐标点
//        for (int i = 0; i < list.size(); i++) {
//            Point point = list.get(i);
////            point.setXpoint(point.getXpoint() * key);
////            point.setYpoint(point.getYpoint() * key);
//        }
//        invalidate();
    }

    public void reduce() {
//        float key = (float) (9.0 / 10);
//        //bitmap的比例缩小
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * key), (int) (height * key), false);
//        bitmapRed = Bitmap.createScaledBitmap(bitmapRed, (int) (width * key), (int) (height * key), false);
//        bitmapWidth = bitmap.getWidth();
//        bitmapHeight = bitmap.getHeight();
//        // 计算坐标点
//        for (int i = 0; i < list.size(); i++) {
//            Point point = list.get(i);
////            point.setXpoint(point.getXpoint() * key);
////            point.setYpoint(point.getYpoint() * key);
//        }
//        invalidate();
    }


    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!clickState) {
                // 点击的状态为正在点击
                clickState = true;
                // 点击的位置
                clickXTemp = e.getX();
                clickYTemp = e.getY();
                Log.e("onSingleTapUp","clickXTemp:" + clickXTemp + ",clickYTemp:" + clickYTemp);
                // 修正点击的位置：如果点击的位置正好在图片范围内，此时让点击的位置为图片的坐标
                for (int i = 0; i < list.size(); i++) {
                    Point item = list.get(i);
                    Log.e("onSingleTapUp","item.x:" + item.x + ",item.y:" + item.y + ",item.checkState:" + item.checkState);
                    if (clickXTemp >= item.x && clickXTemp <= item.x + widthHeight && clickYTemp >= item.y && clickYTemp <= item.y + widthHeight) {
                        if (item.checkState == 1) {//已选
                            item.checkState = 2;//可选
                        } else if (item.checkState == 2) {//可选
                            item.checkState = 1;//已选
                        } else if (item.checkState == 3) {//不可选

                        }
                        clickXTemp = item.x;
                        clickYTemp = item.y;
                        break;
                    }
                }
                // 根据当前图片的宽高来判断是否平滑放大
                // 如果是 < PARAM_RATE倍的原始图片，此时平滑放大
                // 如果是 >= PARAM_RATE倍的原始图片，此时做点击选中或者不选中的效果
                if (widthHeight < originalWidthHeight * PARAM_RATE) {
                    smoothZoomOut();
                } else {
                    // 绘制选装或者不选中的状态
                    invalidate();
                    clickState = false;
                }
            }
            return super.onSingleTapUp(e);
        }
    }

    /**
     * 平滑放大
     */
    public void smoothZoomOut() {
        // 点击的位置
        float clickX = this.clickXTemp;
        float clickY = this.clickYTemp;

        // 对图片的宽高进行放大
        float rate = PARAM_ZOOM_OUT_IN;
//        float oldWidthHeight = this.widthHeight;
////        this.widthHeight = this.widthHeight + 3;
////        rate = this.widthHeight / oldWidthHeight;
        this.widthHeight = this.widthHeight * rate;


        // 根据点击位置+当前的进度对坐标进行重新计算
        for (int i = 0; i < this.list.size(); i++) {
            Point item = list.get(i);
            // 1、计算点击的点和item的距离
            float dx = clickX - item.x;
            float dy = clickY - item.y;
            float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            // 2、计算点击的点和item的角度
            float sinValue = Math.abs(dy / distance);
            float cosValue = Math.abs(dx / distance);
            // 3、计算扩大后的距离
            float longDistance = distance * rate;
            // 4、计算扩大后的位置
            if (dx == 0 && dy == 0) { // 说明是同一个点
            } else if (dx > 0 && dy <= 0) { // 第一象限
                Log.e(TAG, "第一象限");
                item.x = clickX - longDistance * cosValue;
                item.y = clickY + longDistance * sinValue;
            } else if (dx <= 0 && dy <= 0) { // 第二象限
                Log.e(TAG, "第二象限");
                item.x = clickX + longDistance * cosValue;
                item.y = clickY + longDistance * sinValue;
            } else if (dx < 0 && dy > 0) { // 第三象限
                Log.e(TAG, "第三象限");
                item.x = clickX + longDistance * cosValue;
                item.y = clickY - longDistance * sinValue;
            } else if (dx >= 0 && dy > 0) { // 第四象限
                Log.e(TAG, "第四象限");
                item.x = clickX - longDistance * cosValue;
                item.y = clickY - longDistance * sinValue;
            }
        }

        postInvalidate();

        // 绘制条件判断
        if (this.widthHeight < this.originalWidthHeight * PARAM_RATE) {
            handler.sendEmptyMessageDelayed(UPDATA, 0);
        } else {
            // 停止绘制，更新点击状态
            this.clickState = false;
        }
    }

}
