package ca.uoit.crobot;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class AppViewPager extends ViewPager {

    private boolean enableSwipe;

    public AppViewPager(final Context context) {
        super(context);
        init();
    }

    public AppViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        enableSwipe = true;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        return enableSwipe && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return enableSwipe && super.onTouchEvent(event);
    }

    public void setEnableSwipe(final boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
    }
}
