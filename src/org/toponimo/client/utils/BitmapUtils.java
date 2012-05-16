package org.toponimo.client.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.util.Log;

public class BitmapUtils {

	public static final String	TAG	= "BitmapUtils";

	/**
	 * Takes a bitmap and returns a Bitmap object with rounded corners.
	 * 
	 * @param bitmap
	 * @return
	 */

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float cornerRadius) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap scaleBitmapFile(Uri bitmapPath, int width, int height) {
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath.getPath());
		int currentWidth = bitmap.getWidth();
		int currentHeight = bitmap.getHeight();

		if (currentWidth > width && currentHeight > height) {
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			return scaledBitmap;
		} else {
			return bitmap;
		}
	}

	public static String createThumbnail(Uri sourcePath, int xy) {
		String source = sourcePath.getPath();
		String target = source.substring(0, source.length() - 4) + "_thumb.jpg";
		Log.d(TAG + ": target", target);
		File file = new File(target);
		Bitmap bitmap = scaleBitmapFile(sourcePath, xy, xy);
		// bitmap = getRoundedCornerBitmap(bitmap, 8);
		FileOutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileStream);
			fileStream.flush();
			fileStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return target;

	}

}
