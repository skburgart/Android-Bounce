package com.stevekb.sandbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

public class CustomSurface extends SurfaceView implements SurfaceView.OnTouchListener {

	public Circle c;
	private boolean active = true;
	private static final float GRAVITY = 10;
	public float gx, gy;
	Context pc;
	
	public CustomSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		pc=context;

		c = new Circle(300, 300, 30, 20, 50, Color.MAGENTA);
		

		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint cp = new Paint();

 		cp.setColor(Color.BLACK);
		canvas.drawCircle(c.x, c.y, c.radius, cp);
		cp.setColor(c.color);
		canvas.drawCircle(c.x, c.y, c.radius - 5, cp);
	}
	
	public void update(int delta) {
		c.vx += GRAVITY * gx;
		c.vy += GRAVITY * gy;
		c.x += delta/100f * c.vx;
        c.y += delta/100f * c.vy;
        
        if (c.x < c.radius) {
        	c.vx = -c.vx/2;
        	c.x = c.radius;
        } else if (c.x > this.getWidth() - c.radius) {
        	c.vx = -c.vx/2;
        	c.x = this.getWidth() - c.radius;
        }
        
        if (c.y < c.radius) {
        	c.vy = -c.vy/2;
        	c.y = c.radius;
        } else if (c.y > this.getHeight() - c.radius){
        	c.vy = -c.vy/2;
        	c.y = this.getHeight() - c.radius;
        }
        
        postInvalidate();
    }
	
	public void startMyLogicThread() {
        new Thread() {
            public void run() {
                long time1 = System.currentTimeMillis();
                long time2;

                while (active) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    time2 = System.currentTimeMillis();
                    int delta = (int) (time2 - time1);
                    update(delta);
                    time1 = time2;
                }
            }
        }.start();
    }
	
    public void setActive(boolean active) {
        this.active = active;
    }

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			c.vx = 0;
			c.vy = 0;
			c.x = event.getX();
			c.y = event.getY();
			return true;
		}
		return false;
	}
}
