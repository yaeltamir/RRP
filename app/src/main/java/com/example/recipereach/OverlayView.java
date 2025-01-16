package com.example.recipereach;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {
    private List<PointF> points = new ArrayList<>();
    private Paint paint;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED); // צבע אדום
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    // עדכון נקודות חדשות
    public void setPoints(List<PointF> points) {
        this.points = points;
        invalidate(); // עדכון התצוגה
    }
    public void clear(){
        this.points=new ArrayList<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (points != null) {
            for (PointF point : points) {
                canvas.drawCircle(point.x, point.y, 10, paint); // ציור נקודה
            }
        }
    }
}
