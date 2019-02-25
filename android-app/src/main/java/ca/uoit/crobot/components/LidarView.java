package ca.uoit.crobot.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LidarView extends View {

    private final Paint paint = new Paint();

    private float[] angles = new float[0];
    private float[] ranges = new float[0];

    public LidarView(final Context context, final AttributeSet as) {
        super(context, as);
    }

    public void setScan(final float[] angles, final float[] ranges) {
        this.angles = angles;
        this.ranges = ranges;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int imgWidth = getWidth();
        final int imgHeight = imgWidth;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(Color.RED);

        int centerX = imgWidth / 2;
        int centerY = getHeight() / 2;

        if (angles != null && ranges != null) {
            for (int i = 0; i < angles.length; i++) {
                double y = Math.sin(angles[i]) * ranges[i] / 5 * imgWidth + centerY;
                double x = Math.cos(angles[i]) * ranges[i] / 5 * imgHeight + centerX;

                canvas.drawCircle((float) x, (float) y, 5, paint);
            }
        }
    }
}
