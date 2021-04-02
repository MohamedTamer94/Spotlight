package com.takusemba.spotlight;

import android.app.Activity;
import android.graphics.PointF;
import android.view.View;

/**
 * Target
 *
 * @author takusemba
 * @since 26/06/2017
 **/
public class CustomTarget implements Target {

    private PointF point;
    private float radius;
    private View view;
    private static Builder builder;

    /**
     * Constructor
     */
    private CustomTarget(PointF point, float radius, View view) {
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
     * Builder class which makes it easier to create {@link CustomTarget}
     */
    public static class Builder extends AbstractBuilder<Builder, CustomTarget> {

        @Override
        protected Builder self() {
            return this;
        }

        private View view;

        /**
         * Constructor
         */
        public Builder(Activity context) {
            super(context);

            builder = this;
        }

        /**
         * Set the custom view shown on Spotlight
         *
         * @param view view shown on Spotlight
         * @return This Builder
         */
        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        /**
         * Create the {@link CustomTarget}
         *
         * @return the created CustomTarget
         */
        @Override
        public CustomTarget build() {
            PointF point = new PointF(startX, startY);
            return new CustomTarget(point, radius, view);
        }
    }
}
