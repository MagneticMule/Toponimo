package org.toponimo.client.controls;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RecorderView extends ImageView{
    
    private enum currentState {STOPPED, RECORDING, PLAYING};
    
    private Drawable d = new BitmapDrawable();

    public RecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
    }


}
