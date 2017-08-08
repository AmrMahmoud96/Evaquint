package com.evaquint.android.modules;

        import android.content.Context;
        import android.graphics.drawable.Drawable;
        import android.media.Image;
        import android.support.percent.PercentRelativeLayout;
        import android.support.v7.widget.AppCompatButton;
        import android.util.AttributeSet;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;

        import com.evaquint.android.R;

/**
 * Created by henry on 8/7/2017.
 */

public class MaterialButton extends AppCompatButton {
    // internal components
    ImageView icon;
    public MaterialButton(Context context) {
        this(context, null);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        LayoutInflater.from(context).inflate(R.layout., this);
        icon = (ImageView) findViewById(R.id.button_icon);
//        icon.setImageDrawable(new Drawable() {
//        });
    }



}
