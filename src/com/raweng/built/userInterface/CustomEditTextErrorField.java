package com.raweng.built.userInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.raweng.built.utilities.RawAppUtils;

/**
 * custom edit text.
 * 
 * @author raw engineering, Inc
 *
 */
public class CustomEditTextErrorField extends EditText {
    private final String TAG = "CustomEditTextErrorField";


	public CustomEditTextErrorField(Context context) {
        super(context);
    }

    public CustomEditTextErrorField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditTextErrorField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Don't send delete key so edit text doesn't capture it and close error
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (TextUtils.isEmpty(getText().toString()) && keyCode == KeyEvent.KEYCODE_DEL)
            return true;
        else
            return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Keep track of which icon we used last
     */
    private Drawable lastErrorIcon = null;

    
    @Override
    public void setError(CharSequence error, Drawable icon) {
        super.setError(error, icon);
        lastErrorIcon = icon;

        // if the error is not null, and we are in JB, force
        // the error to show
        if (error != null) {
            showErrorIconHax(icon);
        }
    }

    /**
     * In onFocusChanged() we also have to reshow the error icon as the Editor
     * hides it. Because Editor is a hidden class we need to cache the last used
     * icon and use that
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        showErrorIconHax(lastErrorIcon);
    }

    /**
     * Use reflection to force the error icon to show. Dirty but resolves the
     * issue in 4.2
     */
    private void showErrorIconHax(Drawable icon) {
        if (icon == null)
            return;

        // only for JB 4.2 and 4.2.1
        if (android.os.Build.VERSION.SDK_INT != 16 &&
                android.os.Build.VERSION.SDK_INT != 17)
            return;

        try {
            Class<?> textview = Class.forName("android.widget.TextView");
            Field tEditor = textview.getDeclaredField("mEditor");
            tEditor.setAccessible(true);
            Class<?> editor = Class.forName("android.widget.Editor");
            Method privateShowError = editor.getDeclaredMethod("setErrorIcon", Drawable.class);
            privateShowError.setAccessible(true);
            privateShowError.invoke(tEditor.get(this), icon);
        } catch (Exception error) {
        	RawAppUtils.showLog(TAG, error.toString());
        	RawAppUtils.errorLog(TAG, error.toString());
        }
    }
}
