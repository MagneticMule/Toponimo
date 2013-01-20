/**
 * 
 */
package org.toponimo.client.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Thomas Sweeney
 * 
 */
public class DrawingView extends View {
	private Paint	mPaint			= new Paint();
	private Path	mPath			= new Path();
	private Bitmap	mBitmap			= null;
	private Canvas	mCanvas			= null;

	private float	mStrokeWidth	= 5f;

	/**
	 * @param context
	 * @param attrs
	 */
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPath.moveTo(eventX, eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			int historySize = event.getHistorySize();
			for (int i = 0; i < historySize; i++) {
				float historicalX = event.getHistoricalX(i);
				float historicalY = event.getHistoricalY(i);
				mPath.lineTo(historicalX, historicalY);
			}
			mPath.lineTo(eventX, eventY);

			/*
			 * if (mBitmap == null) { mBitmap =
			 * Bitmap.createBitmap(this.getWidth
			 * (),this.getHeight(),Bitmap.Config.ARGB_8888); mCanvas = new
			 * Canvas(mBitmap); }
			 * 
			 * mCanvas.drawPath(mPath, mPaint);
			 */

			break;
		default:
			return false;
		}
		// force a redraw
		invalidate();
		return true;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		canvas.drawPath(mPath, mPaint);
	}

	public void saveCanvas(Canvas canvas) {

	}

	public void expandDrawingRect() {

	}

	public void reduceDrawingRect() {

	}

	public void resetDrawingRect() {

	}

}
