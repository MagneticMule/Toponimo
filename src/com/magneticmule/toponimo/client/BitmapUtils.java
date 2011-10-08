package com.magneticmule.toponimo.client;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.widget.ImageView.ScaleType;

public class BitmapUtils {

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public Bitmap scaleBitmapFile(File file, int width, int height){
		Bitmap bitmap = BitmapFactory.decodeFile(Uri.fromFile(file).toString());
		int currentWidth = bitmap.getWidth();
		int currentHeight = bitmap.getHeight();
		if (currentWidth < width && currentHeight < height){
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			return scaledBitmap;		
		}
		else {return bitmap;}		
	}



}
