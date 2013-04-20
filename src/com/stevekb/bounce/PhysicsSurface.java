package com.stevekb.bounce;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PhysicsSurface extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final boolean DEBUG = false;
    private static final float GRAVITY = 10;
    private static final int MIN_BALL_RADIUS = 20;
    private GameThread thread;
    private ArrayList<Circle> circles;
    private Paint cp;
    private Paint tp;
    Rect currentBounds;
    private float gx, gy;
    private int maxX, maxY;
    private Random rand;
    private final Object circleLock = new Object();

    public PhysicsSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        cp = new Paint(Paint.ANTI_ALIAS_FLAG);
        tp = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentBounds = new Rect();
        rand = new Random();
        circles = new ArrayList<Circle>();
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (circleLock) {
            for (Circle c : circles) {
                cp.setColor(Color.BLACK);
                canvas.drawCircle(c.getX(), c.getY(), c.getRadius(), cp);
                cp.setColor(c.getColor());
                canvas.drawCircle(c.getX(), c.getY(), c.getRadius() - 5, cp);
            }
        }

        // Preview Circle
        if (makingCircle) {
            cp.setColor(Color.BLACK);
            canvas.drawCircle(newX, newY, newRadius, cp);
            cp.setColor(newColor);
            canvas.drawCircle(newX, newY, newRadius - 5, cp);
        }

        // Circle Count
        tp.setColor(Color.WHITE);
        tp.setTextAlign(Align.CENTER);
        tp.setTextSize(42);
        canvas.drawText(circles.size() + " " + getResources().getString(R.string.count_balls), maxX / 2, 42, tp);

        if (DEBUG) {
            String multiLine = "gx: " + gx + "\ngy: " + gy;
            tp.setTextAlign(Align.LEFT);
            tp.setTextSize(30);
            String lines[] = multiLine.split("\n");
            int yoff = 30;
            for (int i = 0; i < lines.length; ++i) {
                canvas.drawText(lines[i], 0, 60 + yoff, tp);
                tp.getTextBounds(lines[i], 0, lines[i].length(), currentBounds);
                yoff += currentBounds.height() + 5;
            }
        }
    }

    public void update(int delta) {

        synchronized (circleLock) {
            for (Circle c : circles) {
                c.setVx(c.getVx() + GRAVITY * gx);
                c.setVy(c.getVy() + GRAVITY * gy);
                c.setX(c.getX() + delta / 100f * c.getVx());
                c.setY(c.getY() + delta / 100f * c.getVy());

                if (c.getX() < c.getRadius()) {
                    c.setVx(-c.getVx() * c.getElasticity());
                    c.setX(c.getRadius());
                } else if (c.getX() > maxX - c.getRadius()) {
                    c.setVx(-c.getVx() * c.getElasticity());
                    c.setX(maxX - c.getRadius());
                }

                if (c.getY() < c.getRadius()) {
                    c.setVy(-c.getVy() * c.getElasticity());
                    c.setY(c.getRadius());
                } else if (c.getY() > maxY - c.getRadius()) {
                    c.setVy(-c.getVy() * c.getElasticity());
                    c.setY(maxY - c.getRadius());
                }
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

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                boolean removed = false;

                for (int i = circles.size() - 1; i >= 0; --i) {
                    if (circles.get(i).isTouching(touchX, touchY)) {
                        removed = true;
                        synchronized (circleLock) {
                            circles.remove(circles.get(i));
                        }
                        break;
                    }
                }

                if (!removed) {
                    newX = touchX;
                    newY = touchY;
                    newRadius = MIN_BALL_RADIUS;
                    newColor = Color.argb(255, rand.nextInt(256),
                            rand.nextInt(256), rand.nextInt(256));
                    makingCircle = true;
                }

                return true;

            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                newRadius = Math.min(Math.max(
                        Circle.distance(newX, newY, endX, endY), MIN_BALL_RADIUS),
                        maxX / 2 - 5);
                return true;

            case MotionEvent.ACTION_UP:
                if (makingCircle) {
                    synchronized (circleLock) {
                        circles.add(new Circle(newX, newY, newRadius, rand
                                .nextFloat() / 4f + 0.75f, newColor));
                    }
                    makingCircle = false;
                }
                return true;
        }
        return false;
    }

    public void setGravity(float gx, float gy) {
        this.gx = gx;
        this.gy = gy;
    }

    public void setThreadRunning(boolean running) {
        thread.setRunning(running);
    }

    public void clearCircles() {
        circles.clear();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        maxX = width;
        maxY = height;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        maxX = getWidth();
        maxY = getHeight();

        thread = new GameThread(this);
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
