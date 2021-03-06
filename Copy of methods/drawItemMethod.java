/**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        TableXYDataset tdataset = (TableXYDataset) dataset;
        PlotOrientation orientation = plot.getOrientation();

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        double[] stack1 = getStackValues(tdataset, series, item);

        // get the previous point and the next point so we can calculate a
        // "hot spot" for the area (used by the chart entity)...
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double[] stack0 = getStackValues(tdataset, series, Math.max(item - 1,
                0));

        int itemCount = dataset.getItemCount(series);
        double x2 = dataset.getXValue(series, Math.min(item + 1,
                itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1,
                itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double[] stack2 = getStackValues(tdataset, series, Math.min(item + 1,
                itemCount - 1));

        double xleft = (x0 + x1) / 2.0;
        double xright = (x1 + x2) / 2.0;
        double[] stackLeft = averageStackValues(stack0, stack1);
        double[] stackRight = averageStackValues(stack1, stack2);
        double[] adjStackLeft = adjustedStackValues(stack0, stack1);
        double[] adjStackRight = adjustedStackValues(stack1, stack2);

        RectangleEdge edge0 = plot.getDomainAxisEdge();

        float transX1 = (float) domainAxis.valueToJava2D(x1, dataArea, edge0);
        float transXLeft = (float) domainAxis.valueToJava2D(xleft, dataArea,
                edge0);
        float transXRight = (float) domainAxis.valueToJava2D(xright, dataArea,
                edge0);

        if (this.roundXCoordinates) {
            transX1 = Math.round(transX1);
            transXLeft = Math.round(transXLeft);
            transXRight = Math.round(transXRight);
        }
        float transY1;

        RectangleEdge edge1 = plot.getRangeAxisEdge();

        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        if (y1 >= 0.0) {  // handle positive value
            transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[1], dataArea,
                    edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[1],
                    dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                    adjStackLeft[1], dataArea, edge1);

            // LEFT POLYGON
            if (y0 >= 0.0) {
                double yleft = (y0 + y1) / 2.0 + stackLeft[1];
                float transYLeft
                    = (float) rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transY1);
                    left.lineTo(transX1, transStack1);
                    left.lineTo(transXLeft, transStackLeft);
                    left.lineTo(transXLeft, transYLeft);
                } else {
                    left.moveTo(transY1, transX1);
                    left.lineTo(transStack1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                    left.lineTo(transYLeft, transXLeft);
                }
                left.closePath();
            } else {
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transStack1);
                    left.lineTo(transX1, transY1);
                    left.lineTo(transXLeft, transStackLeft);
                } else {
                    left.moveTo(transStack1, transX1);
                    left.lineTo(transY1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                }
                left.closePath();
            }

            float transStackRight = (float) rangeAxis.valueToJava2D(
                    adjStackRight[1], dataArea, edge1);
            // RIGHT POLYGON
            if (y2 >= 0.0) {
                double yright = (y1 + y2) / 2.0 + stackRight[1];
                float transYRight
                    = (float) rangeAxis.valueToJava2D(yright, dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transYRight);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transYRight, transXRight);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            }
            else {
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            }
        }
        else {  // handle negative value
            transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[0], dataArea,
                    edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[0],
                    dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                    adjStackLeft[0], dataArea, edge1);

            // LEFT POLYGON
            if (y0 >= 0.0) {
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transStack1);
                    left.lineTo(transX1, transY1);
                    left.lineTo(transXLeft, transStackLeft);
                } else {
                    left.moveTo(transStack1, transX1);
                    left.lineTo(transY1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                }
                left.clone();
            } else {
                double yleft = (y0 + y1) / 2.0 + stackLeft[0];
                float transYLeft = (float) rangeAxis.valueToJava2D(yleft,
                        dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    left.moveTo(transX1, transY1);
                    left.lineTo(transX1, transStack1);
                    left.lineTo(transXLeft, transStackLeft);
                    left.lineTo(transXLeft, transYLeft);
                } else {
                    left.moveTo(transY1, transX1);
                    left.lineTo(transStack1, transX1);
                    left.lineTo(transStackLeft, transXLeft);
                    left.lineTo(transYLeft, transXLeft);
                }
                left.closePath();
            }
            float transStackRight = (float) rangeAxis.valueToJava2D(
                    adjStackRight[0], dataArea, edge1);

            // RIGHT POLYGON
            if (y2 >= 0.0) {
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            } else {
                double yright = (y1 + y2) / 2.0 + stackRight[0];
                float transYRight = (float) rangeAxis.valueToJava2D(yright,
                        dataArea, edge1);
                if (orientation == PlotOrientation.VERTICAL) {
                    right.moveTo(transX1, transStack1);
                    right.lineTo(transX1, transY1);
                    right.lineTo(transXRight, transYRight);
                    right.lineTo(transXRight, transStackRight);
                } else {
                    right.moveTo(transStack1, transX1);
                    right.lineTo(transY1, transX1);
                    right.lineTo(transYRight, transXRight);
                    right.lineTo(transStackRight, transXRight);
                }
                right.closePath();
            }
        }

        //  Get series Paint and Stroke
        Paint itemPaint = getItemPaint(series, item);
        if (pass == 0) {
            g2.setPaint(itemPaint);
            g2.fill(left);
            g2.fill(right);
        }

        // add an entity for the item...
        if (entities != null) {
            // Create the entity area and limit it to the data area
            Area dataAreaHotspot = new Area(left);
            dataAreaHotspot.add(new Area(right));
            dataAreaHotspot.intersect(new Area(dataArea));

            if (!dataAreaHotspot.isEmpty()) {
                addEntity(entities, dataAreaHotspot, dataset, series, item,
                     0.0, 0.0);
            }
        }
    }