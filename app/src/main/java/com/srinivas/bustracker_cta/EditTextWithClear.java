package com.srinivas.bustracker_cta;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class EditTextWithClear extends ConstraintLayout {

    public EditText editText;

    public EditTextWithClear(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EditTextWithClear(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextWithClear(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.edittext_with_clear, this);
        editText = rootView.findViewById(R.id.editText);

        View clearImage = rootView.findViewById(R.id.clearImage);
        clearImage.setOnClickListener(view -> editText.setText(""));
    }
    public void configureText(int bgColor, float size, int color, int typefaceStyle) {
        editText.setBackgroundColor(bgColor);
        editText.setTextSize(size);
        editText.setTextColor(color);
        editText.setTypeface(null, typefaceStyle);
    }
}