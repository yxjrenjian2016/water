package com.example.waterwave.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.waterwave.R;

/**
 * Created on 2016/7/10.
 */
public class Waterwave extends SurfaceView implements SurfaceHolder.Callback {
    private int mTime = 20;
    private int mNumber = 5;
    private AsyncHandler mAsyncHandler;
    private HandlerThread mHandlerThread;
    private int mGradualR = 0;
    private int mSmallCircle;
    private boolean mStop = false;
    private Object mLock = new Object();

    public Waterwave(Context context) {
        super(context);
       //initView();
    }

    public Waterwave(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public Waterwave(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.waterattrs);
        mNumber = ta.getInt(R.styleable.waterattrs_number, 4);//波纹数量，用于控制疏密程度
        mTime = ta.getInt(R.styleable.waterattrs_time, 20);//相邻圆圈变化时间，例如圆圈1到圆圈2的时间，用于控制速率
        mSmallCircle = ta.getInt(R.styleable.waterattrs_small_circle, 5);
        ta.recycle();
    }

    private void initView(Context context ,AttributeSet attributeSet) {
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(-3);
        getAttrs(context,attributeSet);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        synchronized (this.mLock) {
            mHandlerThread = new HandlerThread("waterwave");//异步线程中绘制
            mHandlerThread.start();
            mAsyncHandler = new AsyncHandler(mHandlerThread.getLooper());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        synchronized (this.mLock) {
            if (mHandlerThread != null) {
                mAsyncHandler.removeCallbacksAndMessages(null);
                mAsyncHandler = null;
                mHandlerThread.quit();
                mHandlerThread = null;
            }
            this.mGradualR = 0;
        }
    }

    /**
     * 启动动画
     */
    public void start() {
        if (mAsyncHandler != null)
            mStop = false;
            mAsyncHandler.sendEmptyMessageDelayed(0, mTime);
    }

    /**
     * 暂停动画
     */
    public void stop() {
        mStop = true;
        if (mAsyncHandler != null){
            mAsyncHandler.removeCallbacksAndMessages(null);
        }
    }

    class AsyncHandler extends Handler {
        public AsyncHandler(Looper paramLooper) {
            super(paramLooper);
        }

        public void handleMessage(Message paramMessage) {
            Canvas localCanvas = null;
            super.handleMessage(paramMessage);
            SurfaceHolder localSurfaceHolder = Waterwave.this.getHolder();
            try {
                localCanvas = localSurfaceHolder.lockCanvas();
                if (localCanvas != null) {
                    Paint localPaint1 = new Paint();
                    localPaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    localCanvas.drawPaint(localPaint1);
                    float cirleX = localCanvas.getWidth() / 2;//圆心x坐标,同时是圆的最大半径
                    float circleY = localCanvas.getHeight() / 2;//圆心Y坐标
                    float step = (cirleX - mSmallCircle) / (float)mNumber;//圆之间的距离
                    Paint localPaint2 = new Paint();
                    localPaint2.setColor(getResources().getColor(android.R.color.holo_blue_light));
                    localPaint2.setAntiAlias(true);
                    localPaint2.setStyle(Paint.Style.STROKE);
                    localPaint2.setStrokeWidth(3.0F);
                    mGradualR += 2;
                    //最外侧圆的半径达到画布的一半时,重置绘制
                    if(mSmallCircle + mGradualR + step * (mNumber - 1) >= cirleX){
                        mGradualR = 0;
                    }

                    int i = 0;
                    //画四个圆圈，圆心位置相同，圆的半径随时间变大，透明度随时间变小
                    while (i < mNumber) {
                        float circle =  mSmallCircle + mGradualR + step * i;//圆的半径=初始半径+变化半径+步长
                        localPaint2.setAlpha((int) (255.0F - circle * (255.0F / cirleX)));//
                        localCanvas.drawCircle(cirleX, circleY, circle, localPaint2);
                        i++;

                    }
                }
            } catch (Exception localException2) {
                localException2.printStackTrace();
            } finally {
                try {
                    localSurfaceHolder.unlockCanvasAndPost(localCanvas);
                    if( mStop ){
                        return;
                    }
                    synchronized (mLock) {
                        if ((mAsyncHandler != null) && (mHandlerThread.isAlive())) {
                            mAsyncHandler.removeCallbacksAndMessages(null);
                            mAsyncHandler.sendEmptyMessageDelayed(0, mTime);
                        }

                    }
                } catch (Exception localException3) {

                }
            }
        }
    }
}
