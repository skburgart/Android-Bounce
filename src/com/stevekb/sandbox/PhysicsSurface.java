package com.stevekb.sandbox;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PhysicsSurface extends SurfaceView implements
		SurfaceHolder.Callback {

	public GameThread thread;

	private ArrayList<Circle> circles;
	private static final float GRAVITY = 10;
	public float gx, gy;
	private int maxX, maxY;
	Random rand;

	public PhysicsSurface(Context context, AttributeSet attrs) {
		super(context, attrs);

		rand = new Random();
		circles = new ArrayList<Circle>();
		getHolder().addCallback(this);
		setFocusable(true);
		thread = new GameThread(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint cp = new Paint(Paint.ANTI_ALIAS_FLAG);

		for (Circle c : circles) {
			cp.setColor(Color.BLACK);
			canvas.drawCircle(c.x, c.y, c.radius, cp);
			cp.setColor(c.color);
			canvas.drawCircle(c.x, c.y, c.radius - 5, cp);
		}

		// Preview Circle
		if (makingCircle) {
			cp.setColor(Color.BLACK);
			canvas.drawCircle(newX, newY, newRadius, cp);
			cp.setColor(newColor);
			canvas.drawCircle(newX, newY, newRadius - 5, cp);
		}

		// Circle Count
		Paint tp = new Paint(Paint.ANTI_ALIAS_FLAG);
		tp.setColor(Color.WHITE);
		tp.setTextAlign(Align.CENTER);
		tp.setTextSize(42);
		canvas.drawText(circles.size() + " circles", maxX / 2, 42, tp);
	}

	public void update(int delta) {

		for (Circle c : circles) {
			c.vx += GRAVITY * gx;
			c.vy += GRAVITY * gy;
			c.x += delta / 100f * c.vx;
			c.y += delta / 100f * c.vy;

			if (c.x < c.radius) {
				c.vx = -c.vx * c.elasticity;
				c.x = c.radius;
			} else if (c.x > maxX - c.radius) {
				c.vx = -c.vx * c.elasticity;
				c.x = maxX - c.radius;
			}

			if (c.y < c.radius) {
				c.vy = -c.vy * c.elasticity;
				c.y = c.radius;
			} else if (c.y > maxY - c.radius) {
				c.vy = -c.vy * c.elasticity;
				c.y = maxY - c.radius;
			}
		}

		postInvalidate();
	}

	private int newColor, newRadius;
	private float newX, newY, endX, endY;
	private boolean makingCircle = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX;
		float touchY;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touchX = event.getX();
			touchY = event.getY();
			boolean removed = false;

			for (Circle c : circles) {
				if (touchingCircle(touchX, touchY, c)) {
					removed = true;
					circles.remove(c);
					break;
				}
			}

			if (!removed) {
				newX = touchX;
				newY = touchY;
				newColor = Color.argb(255,
						rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
				makingCircle = true;
			}

			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			endX = event.getX();
			endY = event.getY();
			newRadius = distance(newX, newY, endX, endY);

			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (makingCircle) {
			
			int radius = Math.min(Math.max(newRadius, 20), maxX/2 -5);
				
			circles.add(new Circle(newX, newY, radius, rand
					.nextFloat() / 4f + 0.75f, newColor));
			
			makingCircle = false;
			}
			
			return true;
		}
		
		return false;
	}

	private boolean touchingCircle(float x, float y, Circle c) {
		if (distance(x, y, c.x, c.y) <= c.radius)
			return true;
		return false;
	}

	private int distance(float x1, float y1, float x2, float y2) {
		return (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		maxX = width;
		maxY = height;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		maxX = getWidth();
		maxY = getHeight();

		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {

			}
		}
	}
}
