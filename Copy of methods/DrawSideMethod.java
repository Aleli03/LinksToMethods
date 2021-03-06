/**
     * Draws the side of a pie section.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param arc  the arc.
     * @param front  the front of the pie.
     * @param back  the back of the pie.
     * @param paint  the color.
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param drawFront  draw the front?
     * @param drawBack  draw the back?
     */
    protected void drawSide(Graphics2D g2,
                            Rectangle2D plotArea,
                            Arc2D arc,
                            Area front,
                            Area back,
                            Paint paint,
                            Paint outlinePaint,
                            Stroke outlineStroke,
                            boolean drawFront,
                            boolean drawBack) {

        if (getDarkerSides()) {
             paint = PaintAlpha.darker(paint);
        }

        double start = arc.getAngleStart();
        double extent = arc.getAngleExtent();
        double end = start + extent;

        g2.setStroke(outlineStroke);

        // for CLOCKWISE charts, the extent will be negative...
        if (extent < 0.0) {

            if (isAngleAtFront(start)) {  // start at front

                if (!isAngleAtBack(end)) {

                    if (extent > -180.0) {  // the segment is entirely at the
                                            // front of the chart
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getEndPoint().getX(), plotArea.getY(),
                                    arc.getStartPoint().getX()
                                    - arc.getEndPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // the segment starts at the front, and wraps all
                            // the way around
                            // the back and finishes at the front again
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);

                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));

                        side2.intersect(front);
                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawBack) {
                            g2.fill(back);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawBack) {
                            g2.draw(back);
                        }

                    }
                }
                else {  // starts at the front, finishes at the back (going
                        // around the left side)

                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            }
            else {  // the segment starts at the back (still extending
                    // CLOCKWISE)

                if (!isAngleAtFront(end)) {
                    if (extent > -180.0) {  // whole segment stays at the back
                        if (drawBack) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getStartPoint().getX(), plotArea.getY(),
                                    arc.getEndPoint().getX()
                                    - arc.getStartPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(back);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // starts at the back, wraps around front, and
                            // finishes at back again
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);

                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));

                        side2.intersect(back);

                        g2.setPaint(paint);
                        if (drawBack) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawFront) {
                            g2.fill(front);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawBack) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawFront) {
                            g2.draw(front);
                        }

                    }
                }
                else {  // starts at back, finishes at front (CLOCKWISE)

                    if (drawBack) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }

                    if (drawFront) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                }
            }
        }
        else if (extent > 0.0) {  // the pie sections are arranged ANTICLOCKWISE

            if (isAngleAtFront(start)) {  // segment starts at the front

                if (!isAngleAtBack(end)) {  // and finishes at the front

                    if (extent < 180.0) {  // segment only occupies the front
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getStartPoint().getX(), plotArea.getY(),
                                    arc.getEndPoint().getX()
                                    - arc.getStartPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // segments wraps right around the back...
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);

                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);

                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawBack) {
                            g2.fill(back);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawBack) {
                            g2.draw(back);
                        }

                    }
                }
                else {  // segments starts at front and finishes at back...
                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            }
            else {  // segment starts at back

                if (!isAngleAtFront(end)) {
                    if (extent < 180.0) {  // and finishes at back
                        if (drawBack) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getEndPoint().getX(), plotArea.getY(),
                                    arc.getStartPoint().getX()
                                    - arc.getEndPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(back);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // starts at back and wraps right around to the
                            // back again
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);

                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(back);

                        g2.setPaint(paint);
                        if (drawBack) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawFront) {
                            g2.fill(front);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawBack) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawFront) {
                            g2.draw(front);
                        }

                    }
                }
                else {  // starts at the back and finishes at the front
                        // (wrapping the left side)
                    if (drawBack) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }

                    if (drawFront) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }
                }
            }

        }

    }