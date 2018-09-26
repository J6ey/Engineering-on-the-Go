package com.engotg.creator.engotg;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


public class AppListPreference extends ListPreference{
    public AppListPreference(Context context){
        super(context);
    }

    public AppListPreference(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public AppListPreference(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = view.findViewById(android.R.id.title);
        TextView summaryView = view.findViewById(android.R.id.summary);
        Typeface typeface = Typeface.createFromAsset(getContext().getResources().getAssets(), "fibra_one_light.otf");
        titleView.setTypeface(typeface, Typeface.BOLD);
        titleView.setTextColor(getContext().getResources().getColor(R.color.quizText));
        summaryView.setTypeface(typeface);
        summaryView.setTextColor(getContext().getResources().getColor(R.color.greyText));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            titleView.setTextSize(16);
        } else {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
        }
    }
}
