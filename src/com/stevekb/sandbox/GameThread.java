package com.stevekb.sandbox;

import android.util.Log;

public class GameThread extends Thread {

	private PhysicsSurface surface;
	private boolean running;
	
	public GameThread(PhysicsSurface surface) {
		this.surface = surface;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	@Override
	public void run() {
		super.run();
		
        long time1 = System.currentTimeMillis();
        long time2;

        while (running) {
            try {
                Thread.sleep(34);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            time2 = System.currentTimeMillis();
            int delta = (int) (time2 - time1);
            surface.update(delta);
            time1 = time2;
        }
	}
	
}
