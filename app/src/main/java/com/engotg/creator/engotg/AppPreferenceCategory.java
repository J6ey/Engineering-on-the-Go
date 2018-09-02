package com.engotg.creator.engotg;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AppPreferenceCategory extends PreferenceCategory{

    public AppPreferenceCategory(Context context){
        super(context);
    }

    public AppPreferenceCategory(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public AppPreferenceCategory(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = view.findViewById(android.R.id.title);
        Typeface typeface = Typeface.createFromAsset(getContext().getResources().getAssets(), "fibra_one_light.otf");
        titleView.setTypeface(typeface, Typeface.BOLD);
    }
}
