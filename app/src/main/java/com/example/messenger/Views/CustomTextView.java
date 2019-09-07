package com.example.messenger.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.messenger.R;

public class CustomTextView extends AppCompatTextView {
    private float maxWidth = 0;

    public CustomTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomTextView, defStyle, 0);
        maxWidth = a.getDimension(
                R.styleable.CustomTextView_maxCustomWidth,
                maxWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getLineCount() >= 1) {
            float longestLineWidth = -1;
            for (int lineIndex = 0; lineIndex < getLineCount(); lineIndex++) {
                int lineStartIndex = getLayout().getLineStart(lineIndex);
                int lineEndIndex = getLayout().getLineEnd(lineIndex);
                String currentTextLine;
                if (getText().toString().charAt(lineEndIndex - 1) != ' ')
                    currentTextLine = getText().toString().substring(lineStartIndex, lineEndIndex);
                else
                    currentTextLine = getText().toString().substring(lineStartIndex, lineEndIndex - 1);
                float currentLineWidth = getPaint().measureText(currentTextLine + "__");
                if (longestLineWidth < currentLineWidth)
                    longestLineWidth = currentLineWidth;
            }
            ViewGroup.LayoutParams paramsNew = getLayoutParams();
            paramsNew.width = Math.min((int) maxWidth, (int) longestLineWidth);
            setLayoutParams(paramsNew);
        }
    }
}