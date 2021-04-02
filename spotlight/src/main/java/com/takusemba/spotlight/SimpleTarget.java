package com.takusemba.spotlight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.graphics.Color;

/**
 * Position Target
 *
 * @author takusemba
 * @since 26/06/2017
 **/
public class SimpleTarget implements Target {

    private PointF point;
    private float radius;
    private View view;
    private static Builder builder;

    /**
     * Constructor
     */
    private SimpleTarget(PointF point, float radius, View view) {
        this.point = point;
        this.radius = radius;
        this.view = view;
    }

    @Override
    public PointF getPoint() {
        return point;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public View getView() {
        return view;
    }

    /**
     * Builder class which makes it easier to create {@link SimpleTarget}
     */
    public static class Builder extends AbstractBuilder<Builder, SimpleTarget> {

        @Override
        protected Builder self() {
            return this;
        }

        private static final int ABOVE_SPOTLIGHT = 0;
        private static final int BELOW_SPOTLIGHT = 1;

        private CharSequence title;
        private CharSequence description;
        private Typeface titleTypeface;
        private Typeface descriptionTypeFace;
        private int titleFontSize = 24;
        private int descriptionFontSize = 18;
        private int titleColor = Color.WHITE;
        private int descriptionColor = Color.WHITE;

        /**
         * Constructor
         */
        public Builder(Activity context) {
            super(context);

            builder = this;
        }

        /**
         * Set the title text shown on Spotlight
         *
         * @param title title shown on Spotlight
         * @return This Builder
         */
        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        /**
         * Set the description text shown on Spotlight
         *
         * @param description title shown on Spotlight
         * @return This Builder
         */
        public Builder setDescription(CharSequence description) {
            this.description = description;
            return this;
        }

        public Builder setTitleTypeFace(Typeface typeFace) {
            if (typeFace == null) {
                return this;
            }
            titleTypeface = typeFace;
            return this;
        }
        
        public Builder setDescriptionTypeFace(Typeface typeFace) {
            if (typeFace == null) {
                return this;
            }
            descriptionTypeFace = typeFace;
            return this;
        }

        public Builder setTitleFontSize(int size) {
            titleFontSize = size;
            return this;
        }
        
        public Builder setDescriptionFontSize(int size) {
            descriptionFontSize = size;
            return this;
        }

        public Builder setTitleColor(int argb) {
            titleColor = argb;
            return this;
        }
        
        public Builder setDescriptionColor(int argb) {
            descriptionColor = argb;
            return this;
        }

        /**
         * Create the {@link SimpleTarget}
         *
         * @return the created SimpleTarget
         */
        @Override
        public SimpleTarget build() {
            if (getContext() == null) {
                throw new RuntimeException("context is null");
            }
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            //layout.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
            LinearLayout container = new LinearLayout(getContext());
            container.setOrientation(LinearLayout.VERTICAL);
            //container.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
            layout.addView(container);
            TextView titleTv = new TextView(getContext());
            titleTv.setTextSize(titleFontSize);
            if (titleTypeface != null) {
                titleTv.setTypeface(titleTypeface, Typeface.BOLD);
            } else {
                titleTv.setTypeface(null, Typeface.BOLD);
            }
            titleTv.setTextColor(titleColor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.setMargins(0, 0, 0, 16);
            container.addView(titleTv, params);
            TextView descriptionTv = new TextView(getContext());
            descriptionTv.setTextSize(descriptionFontSize);
            descriptionTv.setTextColor(descriptionColor);
            if (descriptionTypeFace != null) {
              descriptionTv.setTypeface(descriptionTypeFace);
            }
            container.addView(descriptionTv);
            titleTv.setText(title);
            descriptionTv.setText(description);
            PointF point = new PointF(startX, startY);
            calculatePosition(point, radius, layout, container);
            return new SimpleTarget(point, radius, layout);
        }

        /**
         * calculate the position of title and description based off of where the spotlight reveals
         */
        private void calculatePosition(final PointF point, final float radius, View spotlightView, LinearLayout container) {
            float[] areas = new float[2];
            Point screenSize = new Point();
            ((WindowManager) spotlightView.getContext()
                    .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);

            areas[ABOVE_SPOTLIGHT] = point.y / screenSize.y;
            areas[BELOW_SPOTLIGHT] = (screenSize.y - point.y) / screenSize.y;

            int largest;
            if (areas[ABOVE_SPOTLIGHT] > areas[BELOW_SPOTLIGHT]) {
                largest = ABOVE_SPOTLIGHT;
            } else {
                largest = BELOW_SPOTLIGHT;
            }

            final LinearLayout layout = container;
            layout.setPadding(100, 0, 100, 0);
            switch (largest) {
                case ABOVE_SPOTLIGHT:
                    spotlightView.getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    layout.setY(point.y - radius - 100 - layout.getHeight());
                                }
                            });
                    break;
                case BELOW_SPOTLIGHT:
                    layout.setY((int) (point.y + radius + 100));
                    break;
            }
        }
    }
}
