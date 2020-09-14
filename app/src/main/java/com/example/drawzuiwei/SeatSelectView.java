//package com.haidilao.hailehui.weex.component.view;
package com.example.drawzuiwei;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.drawzuiwei.bean.Point;
//import com.haidilao.hailehui.model.Point;
//import com.haidilao.hailehui.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeatSelectView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int UPDATA = 100;
    private static final float CHANGE_LENGTH = 3;
    private Context mContext;//上下文
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA:
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
    private int mZhuoWeiGrayColor = Color.parseColor("#A9A9A9");//灰色;
    //图像
    private Map<String, Bitmap> bitmapMap;//存储图像的Bitmap
    private Bitmap checkBitmap;//选中的Bitmap

    boolean clickState = false;// 点击状态 false:结束 true:正在进行中

    //桌位文字大小
    private int mZhuoWeiFontSize = 12;

    private int touchState;
    private int STATE_TWO_POINT = 100;//两个手指
    private int STATE_ONE_POINT = 200;//一个手指

    private float originalWidthHeight = 60; // 图片宽高
    private float widthHeight = 60; // 图片宽高

    float clickXTemp;//点击的X的位置
    float clickYTemp;//点击的Y的位置

    float PARAM_RATE = 2.5f; // 最终放大的图像是原始尺寸的几倍大小

    private static final String TAG = SeatSelectView.class.getSimpleName();
    private static final String TAG_XX = "xiangxian";


    private ArrayList<Point> list;
    private GestureDetector mGestureDetector;

    private int beforeSelectIndex = -1;//上一时刻选中的下标

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
    private SurfaceHolder holder;//SurfaceView的持有者
    private HandlerThread handlerThread;


    public SeatSelectView(Context context) {
        super(context);
    }

    public SeatSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //初始化画笔
        initPaint();
        //初始化图片
        loadImg();
        holder = this.getHolder();
        //设置SurfaceView的监听，监听是否完成
        holder.addCallback(this);
        // 手势监听器,用来监听单击事件
        MySimpleOnGestureListener myGestureDetectore = new MySimpleOnGestureListener();
        mGestureDetector = new GestureDetector(myGestureDetectore);
    }

    public SeatSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void drawImg(){
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);

        Log.e(TAG,"绘制开始");
        if (list != null) {
            for (int i = 0; i < this.list.size(); i++) {
                Point item = list.get(i);
                float x = item.x;
                float y = item.y;

                //绘制对应的桌位
                //1、设置绘制的宽高
                float width = this.widthHeight;
                float height = this.widthHeight;
                //2、给数据源重新设置状态：当前状态不为可预订，不为已预订，不为选择状态，那么为不可选状态
                if (!TextUtils.equals(item.tableStatus,"0")&&!TextUtils.equals(item.tableStatus,"2")&&!TextUtils.equals(item.tableStatus,Point.MY_CHECK)){
                    item.tableStatus = "1";
                }
                //3、如果是选择状态，绘制红色背景和白色的对勾
                if (TextUtils.equals(item.tableStatus,Point.MY_CHECK)){
                    // 绘制红色背景
                    String key = "reserve" + "_" + item.capacity + "_" + item.position + "_" + "state2"  + "_" + item.fx;
                    Bitmap bitmapTemp = bitmapMap.get(key);
                    Bitmap bitmap = big(bitmapTemp, width, height);
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, x, y, mZhuoWeiPaint);
                    }
                    // 居中白色对勾
                    float duigouWidth = width / 2;
                    float duigouHeight = height / 2;
                    Bitmap bitmapDuiGou = big(checkBitmap,width / 2, height / 2);
                    canvas.drawBitmap(bitmapDuiGou,x + width / 2 - duigouWidth / 2,y + height / 2 - duigouHeight / 2 ,mZhuoWeiPaint);
                } else {
                    //4、如果不是选择状态，根据状态设置不同的图片
                    String key = "reserve" + "_" + item.capacity + "_" + item.position + "_" + "state" + item.tableStatus + "_" + item.fx;
                    Bitmap bitmapTemp = bitmapMap.get(key);
                    Bitmap bitmap = big(bitmapTemp, width, height);
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, x, y, mZhuoWeiPaint);
                    }
                }

                // 绘制对应的座位号
                // 如果不是选择状态，才绘制桌位号
                if (!TextUtils.equals(item.tableStatus,Point.MY_CHECK)){
                    // 1、缩放的比例
                    float rateTemp = this.widthHeight / this.originalWidthHeight;
                    // 2、设置画笔状态
                    mZhuoWeiNumPaint.setTextSize(mZhuoWeiFontSize * rateTemp);
                    if (TextUtils.equals(item.tableStatus,"0") || TextUtils.equals(item.tableStatus,"2")){//可预订和不可预定，画笔设置为白色
                        mZhuoWeiNumPaint.setColor(mZhuoWeiWhiteColor);
                    } else if (!TextUtils.equals(item.tableStatus,"0") && !TextUtils.equals(item.tableStatus,"2")){//不可预定，画笔设置为灰色
                        mZhuoWeiNumPaint.setColor(mZhuoWeiGrayColor);
                    }
                    // 3、求得文字对应的宽高
                    float textHeight = measureTextHeight(mZhuoWeiNumPaint) * 2 / 3;
                    String tableNo = item.tableNo;// 桌号
                    float textWidth = 0;// 桌号的宽度
                    if ((tableNo + "").length() == 1) {
                        textWidth = mZhuoWeiNumPaint.measureText("1");
                    } else if ((tableNo + "").length() == 2) {
                        textWidth = mZhuoWeiNumPaint.measureText("11");
                    } else if ((tableNo + "").length() == 3) {
                        textWidth = mZhuoWeiNumPaint.measureText("111");
                    }
                    // 4、计算中心点的位置
                    float centerX = x + width / 2;
                    float centerY = y + height / 2;
                    // 5、绘制桌位文字
                    canvas.drawText(tableNo, centerX - textWidth / 2, centerY + textHeight / 2, mZhuoWeiNumPaint);
                }
            }
            Log.e(TAG,"绘制完成");
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 把传进来的bitmap对象转换为宽度为x,长度为y的bitmap对象
     */
    public static Bitmap big(Bitmap b, float x, float y) {
        if (b != null) {
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

    public void setData(ArrayList<Point> list) {
        this.list = list;
        Log.e(TAG,"setData");
        // 初始化参数
        initParams();
        Log.e(TAG,"初始化参数完成");
        // 绘制
        //invalidate();
    }

    private void initParams() {
        // 获得容器的宽高
        containerWidth = getWidth();
        containerHeight = getHeight();
        // 获取xpoint和ypoint的最大值和最小值
        MaxMin maxMin = getMaxMin();
        float maxXPoint = maxMin.maxXPoint;
        float minXPoint = maxMin.minXPoint;
        float maxYPoint = maxMin.maxYPoint;
        float minYPoint = maxMin.minYPoint;

        // 求得他们变化的范围
        float changeX = maxXPoint - minXPoint;
        float changeY = maxYPoint - minYPoint;

        // 将他们变化的范围放大1.2倍
        float rate = 1.2f;
        float screenWidth = changeX * rate;
        float screenHeight = changeY * rate;

        // 计算座位在页面上的位置（位置颠倒）
        for (int i = 0; i < this.list.size(); i++) {
            Point item = list.get(i);
            item.x = (item.ypoint - minYPoint + (changeY * rate - changeY) / 2) / screenHeight * this.containerWidth;  // 在y轴占的比例 * 容器的宽度
            item.y = (item.xpoint - minXPoint + (changeX * rate - changeX) / 2) / screenWidth * this.containerHeight;// 在x轴占的比例 * 容器的高度
        }
    }

    /**
     * 求得最大值和最小值
     * @return
     */
    private MaxMin getMaxMin() {
        float maxXPoint = 0;
        float minXPoint = 0;
        float maxYPoint = 0;
        float minYPoint = 0;
        for (int i = 0; i < this.list.size(); i++) {
            Point item = this.list.get(i);
            if (i == 0) {
                maxXPoint = item.xpoint;
                minXPoint = item.xpoint;
                maxYPoint = item.ypoint;
                minYPoint = item.ypoint;
            }
            if (item.xpoint > maxXPoint) {
                maxXPoint = item.xpoint;
            }
            if (item.xpoint < minXPoint) {
                minXPoint = item.xpoint;
            }
            if (item.ypoint > maxYPoint) {
                maxYPoint = item.ypoint;
            }
            if (item.ypoint < minYPoint) {
                minYPoint = item.ypoint;
            }
        }
        return new MaxMin(maxXPoint,minXPoint,maxYPoint,minYPoint);
    }

    /**
     * 过滤数据
     * @param filterData
     */
    public void filterData(ArrayList<String> filterData) {
        if (list != null && filterData != null){//过滤标签
            for (int i = 0; i < list.size(); i++) {
                Point point = list.get(i);
                // 如不不是自己的选中,那么就重置状态到当前状态
                if (!TextUtils.equals(point.tableStatus,Point.MY_CHECK)){
                    point.tableStatus = point.tableStatusCopy;
                }
                // 可预定状态 或者 我自己选择
                if (TextUtils.equals(point.tableStatus,Point.KE_YU_DING) || TextUtils.equals(point.tableStatus,Point.MY_CHECK)){
                    if (!filter(point.tableLableName,filterData)) {
                        point.tableStatus = Point.BU_KE_YU_DING;
                    }
                }
            }
        } else if (list != null && filterData == null || filterData.size() == 0){//重置
            for (int i = 0; i < list.size(); i++) {
                Point point = list.get(i);
                if (!TextUtils.equals(point.tableStatus,Point.MY_CHECK)){
                    point.tableStatus = point.tableStatusCopy;
                }
            }
        }
        invalidate();
    }

    /**
     * 是否是可预订
     * @return
     */
    public boolean filter(String tableLableName,ArrayList<String> list){
        boolean result = true;
        if (list != null){
            for (int i = 0; i < list.size(); i++) {
                if (!tableLableName.contains(list.get(i))){
                    result = false;
                    break;
                }
            }
        }
        return  result;
    }

    public static final int MESSAGE_DRAW = 0;

    private void refresh() {
        Message message = Message.obtain();
        message.what = MESSAGE_DRAW;
        handler.removeMessages(MESSAGE_DRAW);
        handler.sendMessage(message);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG,"surfaceCreated");
//        handlerThread = new HandlerThread(TAG);
//        handlerThread.start();
        drawImg();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        handlerThread.quit();
//        handlerThread = null;
    }

    class MaxMin{

        float maxXPoint = 0;
        float minXPoint = 0;
        float maxYPoint = 0;
        float minYPoint = 0;

        public MaxMin(float maxXPoint, float minXPoint, float maxYPoint, float minYPoint) {
            this.maxXPoint = maxXPoint;
            this.minXPoint = minXPoint;
            this.maxYPoint = maxYPoint;
            this.minYPoint = minYPoint;
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
        //座位的Bitmap
        bitmapMap = new HashMap<>();
        //reserve_4_0_stat0_1000
        String[] name = {"reserve_4", "reserve_6", "reserve_8", "reserve_10", "reserve_12"};
        String[] checkState = {"state0", "state1", "state2"}; //0:可预订 2:已预定 其它:不可预定
        String[] positionState = {"1000", "1001", "1002", "1003"};
        // 包间
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < name.length; j++) {
                for (int k = 0; k < checkState.length; k++) {
                    for (int l = 0; l < positionState.length; l++) {
                        String key = name[j] + "_" + i + "_" + checkState[k] + "_" + positionState[l];
                        bitmapMap.put(key, BitmapFactory.decodeResource(getResources(), getDrawableResId(key)));
                    }
                }
            }
        }
        // 选中的Bitmap
        checkBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.reserve_duihao);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 桌号画笔
        mZhuoWeiNumPaint = new Paint();
        mZhuoWeiNumPaint.setAntiAlias(true);
        mZhuoWeiNumPaint.setTextSize(mZhuoWeiFontSize);
        mZhuoWeiNumPaint.setTypeface(Typeface.MONOSPACE); //等宽字体类型
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
                // 不能继续放大条件:当图片的宽高 > PARAM_RATE 倍的原始图片宽高 并且 当前的状态是放大的状态
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
//                invalidate();
                drawImg();
                // 保存上一时刻的信息
                beforePointOneX = nowPointOneX;
                beforePointOneY = nowPointOneY;
                beforePointTwoX = nowPointTwoX;
                beforePointTwoY = nowPointTwoY;
            }
        } else {
            //单个手指
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX = event.getX();
                downY = event.getY();
                this.touchState = STATE_ONE_POINT;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (touchState == STATE_ONE_POINT) {
                    float x = (event.getX() - downX);
                    float y = (event.getY() - downY);
                    // 计算数据里面的x值的最大值和最小值和y值对应的最大值和最小值
                    float minX = 0;
                    float maxX = 0;
                    float minY = 0;
                    float maxY = 0;
                    for (int i = 0; i < list.size(); i++) {
                        Point point = list.get(i);
                        if (i == 0) {
                            minX = point.x;
                            minY = point.y;
                            maxX = point.x;
                            maxY = point.y;
                        }
                        if (point.x > maxX) {
                            maxX = point.x;
                        }
                        if (point.y > maxY) {
                            maxY = point.y;
                        }
                        if (point.x < minX) {
                            minX = point.x;
                        }
                        if (point.y < minY) {
                            minY = point.y;
                        }
                    }
                    if (x > 0 && minX > 50) {//向右
                    } else if (x < 0 && maxX < containerWidth - widthHeight - 50) {//向左
                    } else {
                        //重新计算位置
                        for (int i = 0; i < list.size(); i++) {
                            Point point = list.get(i);
                            point.x = point.x + x;
                        }
                    }

                    if (y > 0 && minY > 50) {//向下
                    } else if (y < 0 && maxY < containerHeight - widthHeight - 50) {//向上
                    } else {
                        //重新计算位置
                        for (int i = 0; i < list.size(); i++) {
                            Point point = list.get(i);
                            //point.x = point.x + x;
                            point.y = point.y + y;
                        }
                    }
                    downX = event.getX();
                    downY = event.getY();
                }
            }
        }
        //invalidate();
        drawImg();
        return true;
    }

    // 计算两个点的距离
    public float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt((x * x) + (y * y));
    }

    //点击
    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 如果不是点击状态，才执行逻辑(点击状态下，可能在执行平滑放大的操作)
            if (!clickState) {
                // 点击的状态为正在点击
                clickState = true;
                // 点击的位置
                clickXTemp = e.getX();
                clickYTemp = e.getY();

                // 获取的点击下标和点击的位置
                int selectIndex = -1;
                for (int i = 0; i < list.size(); i++) {
                    Point item = list.get(i);
                    if (clickXTemp >= item.x && clickXTemp <= item.x + widthHeight && clickYTemp >= item.y && clickYTemp <= item.y + widthHeight) {
                        //0:可预订 2:已预定 其它:不可预定
                        if (TextUtils.equals(item.tableStatus, "0")) {//可预订
                            selectIndex = i;
                        } else if (TextUtils.equals(item.tableStatus, Point.MY_CHECK)){
                            selectIndex = i;
                        }
                        clickXTemp = item.x;
                        clickYTemp = item.y;
                        break;
                    }
                }

                // 点击了可选按钮或者点击了选中的按钮
                if (selectIndex != -1) {
                    // 选中了已选,那么让它变成可选
                    // 如果选中了可选，得考虑首先将所有的变成可选，然后在将这个变成已选
                    if (TextUtils.equals(list.get(selectIndex).tableStatus, Point.MY_CHECK)) { // 已选
                        list.get(selectIndex).tableStatus = "0"; // 可选
                        beforeSelectIndex = -1;
                    } else if (TextUtils.equals(list.get(selectIndex).tableStatus, "0")) { // 可选
                        // 让上一个已选的变成可选
                        if (beforeSelectIndex != -1){
                            list.get(beforeSelectIndex).tableStatus = "0";
                        }
                        // 让点击的变成已选
                        list.get(selectIndex).tableStatus = Point.MY_CHECK;
                        // 保存当前已选的下标
                        beforeSelectIndex = selectIndex;
                    }
                }

                // 根据当前图片的宽高来判断是否平滑放大
                // 如果是 < PARAM_RATE倍的原始图片，此时平滑放大
                // 如果是 >= PARAM_RATE倍的原始图片，此时做点击选中或者不选中的效果
                if (widthHeight < originalWidthHeight * PARAM_RATE) {
                    smoothZoomOut();
                } else {
                    //invalidate();
                    drawImg();
                    clickState = false;
                }
            }

            //获取点击的座位数
            String tableNo = "";
            for (int i = 0; i < list.size(); i++) {
                Point point = list.get(i);
                if (TextUtils.equals(point.tableStatus,Point.MY_CHECK)){
                    tableNo = point.tableNo;
                    break;
                }
            }
            if (mListener != null){
                mListener.onClick(tableNo);
            }

            return super.onSingleTapUp(e);
        }
    }

    /**
     * 平滑放大
     */
    public void smoothZoomOut() {
        //1、点击的位置
        float clickX = this.clickXTemp;
        float clickY = this.clickYTemp;
        //2、图片放大部分
        float oldWidthHeight = this.widthHeight;
        float newWidthHeight = this.widthHeight + CHANGE_LENGTH;
        //3、根据放大的图片计算放大的比例
        float rate = newWidthHeight / oldWidthHeight;
        this.widthHeight = this.widthHeight * rate;
        // 4、根据点击位置+当前的放大比例进行重新计算坐标
        for (int i = 0; i < this.list.size(); i++) {
            Point item = list.get(i);
            // 4.1、计算点击的点和item的距离
            float dx = clickX - item.x;
            float dy = clickY - item.y;
            float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            // 4,2、计算点击的点和item的角度
            float sinValue = Math.abs(dy / distance);
            float cosValue = Math.abs(dx / distance);
            // 4.3、计算扩大后的距离
            float longDistance = distance * rate;
            // 4、计算扩大后的位置
            if (dx == 0 && dy == 0) { // 说明是同一个点
            } else if (dx > 0 && dy <= 0) { // 第一象限
                Log.e(TAG_XX, "第一象限");
                item.x = clickX - longDistance * cosValue;
                item.y = clickY + longDistance * sinValue;
            } else if (dx <= 0 && dy <= 0) { // 第二象限
                Log.e(TAG_XX, "第二象限");
                item.x = clickX + longDistance * cosValue;
                item.y = clickY + longDistance * sinValue;
            } else if (dx < 0 && dy > 0) { // 第三象限
                Log.e(TAG_XX, "第三象限");
                item.x = clickX + longDistance * cosValue;
                item.y = clickY - longDistance * sinValue;
            } else if (dx >= 0 && dy > 0) { // 第四象限
                Log.e(TAG_XX, "第四象限");
                item.x = clickX - longDistance * cosValue;
                item.y = clickY - longDistance * sinValue;
            }
        }
        // 5、绘制
        drawImg();
        // 6、根据条件判断是否要继续绘制
        if (this.widthHeight < this.originalWidthHeight * PARAM_RATE) {
            handler.sendEmptyMessageDelayed(UPDATA, 0);
        } else {
            // 停止绘制，更新点击状态
            this.clickState = false;
        }
    }

    //点击事件
    public interface OnViewClickListener {
        void onClick(String tableNo);
    }

    public OnViewClickListener mListener;

    public void setOnViewClickListener(OnViewClickListener listener){
        mListener = listener;
    }
}
