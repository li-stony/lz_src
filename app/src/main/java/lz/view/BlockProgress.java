package lz.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import lz.mylife.R;

/**
 * Created by cussyou on 2016-05-18.
 */
public class BlockProgress extends View implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    Paint p1;
    Paint p2;
    public BlockProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        p1 = new Paint();
        p1.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        p1.setStyle(Paint.Style.FILL);
        p1.setAntiAlias(true);
        p1.setStrokeWidth(2);
        p2 = new Paint();
        p2.setColor(0xffffffff);
        p2.setStyle(Paint.Style.STROKE);
        p2.setAntiAlias(true);
        p2.setStrokeWidth(2);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(2000);
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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p1);
        int h = canvas.getHeight();
        int top = h / 3;
        int bottom = top * 2;
        int w = canvas.getWidth();
        int len = w / 5;
        float value = (Float)animator.getAnimatedValue();
        int startX = (int)(value * w);
        int endX = startX + len;
        if(endX > w) {
            canvas.drawLine(0, top, endX-w, top, p2);
            canvas.drawLine(startX, top, w, top, p2);
            canvas.drawLine(w, bottom, w + w -endX, bottom, p2);
            canvas.drawLine(w-startX, bottom, 0, bottom, p2);
        } else {
            canvas.drawLine(startX, top, startX + len, top, p2);
            canvas.drawLine(w-startX, bottom, w-startX - len, bottom, p2);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        this.postInvalidate();
    }
}
