package main.java.desk;

import main.java.desk.control.Frame1Control;
import main.java.desk.control.FrameAuthControl;

/** エントリーポイントクラス
	@author none **/
public class HttpsRestClientSwing implements Runnable {

	public static void main(String[] args) {
		var thread = new Thread(new HttpsRestClientSwing());
		thread.setUncaughtExceptionHandler(new OriginalUncaughtException());
		thread.start();
	}

	@Override
	public void run() {
		var f = new Frame1Control();
		f.goEvent();
		//var auth = new FrameAuthControl();
		//auth.goEvent();
	}
}