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
    private List<PointF> points = new ArrayList<>(); // List to store detected points
    private Paint paint; // Paint object for drawing

    // Constructor for XML-based instantiation
    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(); // Initialize paint settings
    }

    // Constructor for programmatic instantiation
    public OverlayView(Context context) {
        super(context);
        init(); // Initialize paint settings
    }

    // Initialize the paint object
    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED); // צבע אדום (Red color)
        paint.setStyle(Paint.Style.FILL); // Fill style for circles
        paint.setAntiAlias(true); // Enable anti-aliasing for smoother rendering
    }

    // Update the list of points and refresh the view
    public void setPoints(List<PointF> points) {
        this.points = points;
        invalidate(); // Request a redraw
    }

    // Clear the points list and refresh the view
    public void clear() {
        this.points = new ArrayList<>();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw circles for each detected point
        if (points != null) {
            for (PointF point : points) {
                canvas.drawCircle(point.x, point.y, 10, paint); // ציור נקודה (Draw point)
            }
        }
    }
}
