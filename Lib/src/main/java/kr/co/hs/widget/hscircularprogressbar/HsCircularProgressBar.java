package kr.co.hs.widget.hscircularprogressbar;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import kr.co.hs.widget.hscircularprogressbar.utils.AnimatorUtils;


/**
 * Created by Mikhael LOPEZ on 16/10/2015.
 */
public class HsCircularProgressBar extends View {

    // Properties
//    private float startProgress = 0;
    private float progress = 0;
    private float strokeWidth = getResources().getDimension(R.dimen.default_stroke_width);
    private float backgroundStrokeWidth = getResources().getDimension(R.dimen.default_background_stroke_width);
    private int color = Color.BLACK;
    private int backgroundColor = Color.GRAY;

    // Object used to draw
    private int startAngle = -90;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;

    private boolean isDeterminate = true;
    private float inDeterminateRadius1 = 0;
    private float inDeterminateRadius2 = 0;
    private Paint[] inDeterminatePaints;
    private Paint inDeterminateCurrentPaint;


    private com.nineoldandroids.animation.Animator[] mAnimators;
    private RingPathTransform mRingPathTransform = new RingPathTransform(this);
    private RingRotation mRingRotation = new RingRotation(this);

    //region Constructor & Init Method
    public HsCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HsCircularProgressBar, 0, 0);
        //Reading values from the XML layout
        try {
            // Value
            progress = typedArray.getFloat(R.styleable.HsCircularProgressBar_cpb_progress, progress);
            // StrokeWidth
            strokeWidth = typedArray.getDimension(R.styleable.HsCircularProgressBar_cpb_progressbar_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(R.styleable.HsCircularProgressBar_cpb_background_progressbar_width, backgroundStrokeWidth);
            // Color
            color = typedArray.getInt(R.styleable.HsCircularProgressBar_cpb_progressbar_color, color);
            backgroundColor = typedArray.getInt(R.styleable.HsCircularProgressBar_cpb_background_progressbar_color, backgroundColor);
        } finally {
            typedArray.recycle();
        }

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);

        // Init Foreground
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);


        mAnimators = new com.nineoldandroids.animation.Animator[] {
                AnimatorUtils.createIndeterminate(mRingPathTransform),
                AnimatorUtils.createIndeterminateRotation(mRingRotation)
        };
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(rectF, backgroundPaint);
        if(isDeterminate){
            float angle = 360 * progress / 100;
            canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);
        }else{
            drawRing(canvas, inDeterminateCurrentPaint);
        }
    }
    //endregion


    private void drawRing(Canvas canvas, Paint paint) {

        int saveCount = canvas.save();
        canvas.rotate(mRingRotation.mRotation, canvas.getWidth()/2, canvas.getHeight()/2);

        // startAngle starts at 3 o'clock on a watch.
        float startAngle = -90 + 360 * (mRingPathTransform.mTrimPathOffset
                + mRingPathTransform.mTrimPathStart);
        float sweepAngle = 360 * (mRingPathTransform.mTrimPathEnd
                - mRingPathTransform.mTrimPathStart);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        canvas.restoreToCount(saveCount);
    }

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = (strokeWidth > backgroundStrokeWidth) ? strokeWidth : backgroundStrokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }
    //endregion

    //region Method Get/Set
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.isDeterminate = true;
        for(int i=0;i<mAnimators.length;i++){
            mAnimators[i].end();
        }
        this.progress = (progress<=100) ? progress : 100;
        invalidate();
    }

    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }
    //endregion

    //region Other Method
    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link HsCircularProgressBar#setProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     */
    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link HsCircularProgressBar#setProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     * @param duration The length of the animation, in milliseconds.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
    //endregion

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setIndeterminate(int colors){
        this.isDeterminate = false;

        // Init Foreground
        this.inDeterminateCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.inDeterminateCurrentPaint.setColor(colors);
        this.inDeterminateCurrentPaint.setStyle(Paint.Style.STROKE);
        this.inDeterminateCurrentPaint.setStrokeWidth(strokeWidth);

        for(int i=0;i<mAnimators.length;i++){
            mAnimators[i].start();
        }
    }
    public boolean isIndeterminate(){
        return !this.isDeterminate;
    }






    private static class RingPathTransform {

        public View target;
        public float mTrimPathStart;
        public float mTrimPathEnd;
        public float mTrimPathOffset;

        public RingPathTransform(View target) {
            this.target = target;
        }

        @Keep
        @SuppressWarnings("unused")
        public void setTrimPathStart(float trimPathStart) {
            mTrimPathStart = trimPathStart;
            this.target.invalidate();
        }

        @Keep
        @SuppressWarnings("unused")
        public void setTrimPathEnd(float trimPathEnd) {
            mTrimPathEnd = trimPathEnd;
            this.target.invalidate();
        }

        @Keep
        @SuppressWarnings("unused")
        public void setTrimPathOffset(float trimPathOffset) {
            mTrimPathOffset = trimPathOffset;
            this.target.invalidate();
        }
    }

    private static class RingRotation {

        private float mRotation;
        public View target;

        public RingRotation(View target) {
            this.target = target;
        }

        @Keep
        @SuppressWarnings("unused")
        public void setRotation(float rotation) {
            mRotation = rotation;
            this.target.invalidate();
        }
    }
}
