package ca.uoit.crobot.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {

    private Bitmap map;

    public MapView(final Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(final int dim, final byte[] map) {
        final Bitmap bitmap = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < map.length; i++) {
            bitmap.setPixel(i % dim, i / dim, Color.rgb(map[i], map[i], map[i]));
        }

        synchronized (this) {
            this.map = Bitmap.createScaledBitmap(bitmap, getWidth(), getWidth(), true);
        }

        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (map != null) {
            synchronized (this) {
                canvas.drawBitmap(map, 0, (getHeight() - getWidth()) / 2f, null);
            }
        }
    }
}
