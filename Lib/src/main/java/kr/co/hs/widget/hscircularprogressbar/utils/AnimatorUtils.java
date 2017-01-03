package kr.co.hs.widget.hscircularprogressbar.utils;


import android.graphics.Path;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * Created by Bae on 2016-12-09.
 */
public class AnimatorUtils {

    public static Animator createIndeterminate(Object target) {

        ObjectAnimator trimPathStartAnimator = ObjectAnimator.ofFloat(target, "trimPathStart", 0,
                0.75f);
        trimPathStartAnimator.setDuration(1333);
        trimPathStartAnimator.setInterpolator(Interpolators.TRIM_PATH_START.INSTANCE);
        trimPathStartAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator trimPathEndAnimator = ObjectAnimator.ofFloat(target, "trimPathEnd", 0,
                0.75f);
        trimPathEndAnimator.setDuration(1333);
        trimPathEndAnimator.setInterpolator(Interpolators.TRIM_PATH_END.INSTANCE);
        trimPathEndAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator trimPathOffsetAnimator = ObjectAnimator.ofFloat(target, "trimPathOffset", 0,
                0.25f);
        trimPathOffsetAnimator.setDuration(1333);
        trimPathOffsetAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        trimPathOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(trimPathStartAnimator, trimPathEndAnimator,
                trimPathOffsetAnimator);
        return animatorSet;
    }


    public static Animator createIndeterminateRotation(Object target) {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(target, "rotation", 0, 720);
        rotationAnimator.setDuration(6665);
        rotationAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return rotationAnimator;
    }



    public static class Interpolators {
        public static class TRIM_PATH_START {
            // L 0.5,0
            // C 0.7,0 0.6,1 1,1
            private static final Path PATH_TRIM_PATH_START;
            static {
                PATH_TRIM_PATH_START = new Path();
                PATH_TRIM_PATH_START.lineTo(0.5f, 0);
                PATH_TRIM_PATH_START.cubicTo(0.7f, 0, 0.6f, 1, 1, 1);
            }
            public static final Interpolator INSTANCE =
                    PathInterpolatorCompat.create(PATH_TRIM_PATH_START);
        }

        public static class TRIM_PATH_END {
            // C 0.2,0 0.1,1 0.5,1
            // L 1,1
            private static final Path PATH_TRIM_PATH_END;
            static {
                PATH_TRIM_PATH_END = new Path();
                PATH_TRIM_PATH_END.cubicTo(0.2f, 0, 0.1f, 1, 0.5f, 1);
                PATH_TRIM_PATH_END.lineTo(1, 1);
            }
            public static final Interpolator INSTANCE = PathInterpolatorCompat.create(PATH_TRIM_PATH_END);
        }


        public static class LINEAR {
            public static final Interpolator INSTANCE = new LinearInterpolator();
        }

        private Interpolators() {}
    }
}
