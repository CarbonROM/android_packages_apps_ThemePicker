/*
 * Copyright (C) 2020 CarbonROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.customization.model.theme.custom;

import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;

public class TwoTrianglesDrawable extends ShapeDrawable {

    private int mActivePrimary;
    private int mSecondPrimary;

    public TwoTrianglesDrawable(int active, int alternative, boolean circle){
        super();
        mActivePrimary = active;
        mSecondPrimary = alternative;
        if (circle) {
            setShape(new CircleTwoTrianglesShape());
        } else {
            setShape(new TwoTrianglesShape());
        }
    }

    private class TwoTrianglesShape extends Shape {

        @Override
        public void draw(Canvas canvas, Paint paint) {

            Path path = new Path();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            Path path1 = new Path();
            path1.setFillType(Path.FillType.INVERSE_EVEN_ODD);

            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            paint.setColor(mActivePrimary);

            Point a = new Point((int)getWidth(),0);
            Point b = new Point((int)getWidth(), (int)getHeight());
            Point c = new Point(0, (int) getHeight());

            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();
            canvas.drawPath(path, paint);

            paint.setColor(mSecondPrimary);

            Point a1 = new Point((int)getWidth(),0);
            Point b1 = new Point(0, 0);
            Point c1 = new Point(0, (int) getHeight());

            path1.moveTo(a1.x, a1.y);
            path1.lineTo(b1.x, b1.y);
            path1.lineTo(c1.x, c1.y);
            path1.close();
            canvas.drawPath(path1, paint);

        }
    }

    private class CircleTwoTrianglesShape extends Shape {

        @Override
        public void draw(Canvas canvas, Paint paint) {

            Path path = new Path();
            path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            Path path1 = new Path();
            path1.setFillType(Path.FillType.INVERSE_EVEN_ODD);

            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            paint.setColor(mActivePrimary);

            Point a = new Point((int)getWidth(),0);
            Point b = new Point((int)getWidth(), (int)getHeight());
            Point c = new Point(0, (int) getHeight());

            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();
            canvas.drawPath(path, paint);

            paint.setColor(mSecondPrimary);

            Point a1 = new Point((int)getWidth(),0);
            Point b1 = new Point(0, 0);
            Point c1 = new Point(0, (int) getHeight());

            path1.moveTo(a1.x, a1.y);
            path1.lineTo(b1.x, b1.y);
            path1.lineTo(c1.x, c1.y);
            path1.close();
            canvas.drawPath(path1, paint);

            // Keeps the source pixels that cover the destination pixels,
            // discards the remaining source and destination pixels.
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0XFF000000);

            Path cpath = new Path();
            float radius = getWidth() > getHeight() ? getWidth() : getHeight();
            cpath.addCircle(getWidth() / 2, getHeight() / 2, radius, Path.Direction.CW);

            canvas.drawPath(cpath, paint);
        }
    }
}
