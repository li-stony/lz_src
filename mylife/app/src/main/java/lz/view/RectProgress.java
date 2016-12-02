package lz.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import lz.mylife.R;

/**
 * Created by cussyou on 2016-06-03.
 */
public class RectProgress extends View implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    Paint p1;
    Paint p2;
    Paint p3;

    public RectProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        p1 = new Paint();
        p1.setColor( 0xe0D32F2F);
        p1.setStyle(Paint.Style.FILL);
        p1.setAntiAlias(true);
        p1.setStrokeWidth(2);
        p2 = new Paint();
        p2.setColor(0xffffffff);
        p2.setStyle(Paint.Style.STROKE);
        p2.setAntiAlias(true);
        p2.setStrokeWidth(3);
        p3 = new Paint();
        p3.setColor(0xffffffff);
        p3.setStyle(Paint.Style.FILL);
        p3.setAntiAlias(true);
        p3.setStrokeWidth(5);
    }


    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(6000);
        animator.setStartDelay((long) (1000 * Math.random()));
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(this);
        animator.start();
    }
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(animator != null) {
            animator.cancel();
        }
    }

    private float[] getPoint(float[] point1, float[] point2, float offset){
        float[] point = new float[2];
        point[0] = offset*(point2[0] - point1[0]) + point1[0];
        point[1] = offset*(point2[1] - point1[1]) + point1[1];
        return point;
    }
    private float calcLen(float[] start, float[] end) {
        double len = Math.sqrt((end[0]-start[0])*(end[0]-start[0]) + (end[1]-start[1])*(end[1]-start[1]));
        return (float)len;
    }
    private int findSegIndex ( float[] v, float value){
        for(int i = 0; i<v.length; i++) {
            if(value < v[i]) {
                return i-1;
            }
        }
        return 0;
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p1);
        int len = canvas.getHeight() * 2 / 3;
        int x1 = canvas.getWidth()/4;
        int x2 = canvas.getWidth()/2 + x1;
        int y1 = canvas.getHeight()/4;
        int y2 = canvas.getHeight()/2 + y1;

        int circleLen = 2*(x2-x1 + y2 - y1);

        float[][] coord = new float[][] {
            {x1,y1},{x2,y1},{x2,y2},{x1,y2}
        };
        float v[] = new float[coord.length+1];
        v[0] = 0;
        v[4] = circleLen;
        for(int i=1; i<coord.length; i++) {
            int ind1 = i-1;
            int ind2 = i;
            v[i] = calcLen(coord[ind1], coord[ind2]);
        }
        for(int i=1;i<v.length-1;i++){
            v[i] = v[i] + v[i-1];
        }
        /*
        for(int i=0;i<v.length;i++) {
            v[i] = v[i] / circleLen;
        }
        */
        float startValue = circleLen * (Float) animator.getAnimatedValue();
        float endValue = startValue + len;
        if (endValue > circleLen) {
            endValue -= circleLen;
        }
        int startInd = findSegIndex(v, startValue) ;
        int endInd = findSegIndex(v, endValue) ;

        // draw start point
        {
            float offset = 0;
            float[] point = null;
            offset = (startValue - v[startInd]) / (v[startInd+1] - v[startInd]);
            point = getPoint(coord[startInd], coord[(startInd+1)%coord.length], offset);
            canvas.drawLine(point[0], point[1], coord[(startInd+1)%coord.length][0], coord[(startInd+1)%coord.length][1], p2);
        }
        // draw end point
        {
            float offset = 0;
            float[] point = null;
            offset = (endValue - v[endInd]) / (v[endInd+1] - v[endInd]);
            point = getPoint(coord[endInd], coord[(endInd+1)%coord.length], offset);
            canvas.drawLine(coord[endInd][0], coord[endInd][1], point[0], point[1],  p2);
            canvas.drawCircle(point[0], point[1], 5.0f, p3);
        }
        // draw middle seg
        if(endInd < startInd) {
            endInd += coord.length;
        }
        for(int i=(startInd+1);i<endInd;i++){
            int ind1 = i % coord.length;
            int ind2 = (i+1) % coord.length;
            canvas.drawLine(coord[ind1][0],coord[ind1][1], coord[ind2][0], coord[ind2][1], p2);
        }


    }
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        this.postInvalidate();
    }

}
