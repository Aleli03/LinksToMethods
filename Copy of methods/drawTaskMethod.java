 /**
     * Draws a single task.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawTask(Graphics2D g2,
                            CategoryItemRendererState state,
                            Rectangle2D dataArea,
                            CategoryPlot plot,
                            CategoryAxis domainAxis,
                            ValueAxis rangeAxis,
                            GanttCategoryDataset dataset,
                            int row,
                            int column) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

        // Y0
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(),
                dataArea, rangeAxisLocation);

        // Y1
        Number value1 = dataset.getStartValue(row, column);
        if (value1 == null) {
            return;
        }
        double java2dValue1 = rangeAxis.valueToJava2D(value1.doubleValue(),
                dataArea, rangeAxisLocation);

        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
            value1 = value0;
        }

        double rectStart = calculateBarW0(plot, orientation, dataArea,
                domainAxis, state, row, column);
        double rectBreadth = state.getBarWidth();
        double rectLength = Math.abs(java2dValue1 - java2dValue0);

        Rectangle2D bar = null;
        RectangleEdge barBase = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(java2dValue0, rectStart, rectLength,
                    rectBreadth);
            barBase = RectangleEdge.LEFT;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(rectStart, java2dValue1, rectBreadth,
                    rectLength);
            barBase = RectangleEdge.BOTTOM;
        }

        Rectangle2D completeBar = null;
        Rectangle2D incompleteBar = null;
        Number percent = dataset.getPercentComplete(row, column);
        double start = getStartPercent();
        double end = getEndPercent();
        if (percent != null) {
            double p = percent.doubleValue();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                completeBar = new Rectangle2D.Double(java2dValue0,
                        rectStart + start * rectBreadth, rectLength * p,
                        rectBreadth * (end - start));
                incompleteBar = new Rectangle2D.Double(java2dValue0
                        + rectLength * p, rectStart + start * rectBreadth,
                        rectLength * (1 - p), rectBreadth * (end - start));
            }
            else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                completeBar = new Rectangle2D.Double(rectStart + start
                        * rectBreadth, java2dValue1 + rectLength * (1 - p),
                        rectBreadth * (end - start), rectLength * p);
                incompleteBar = new Rectangle2D.Double(rectStart + start
                        * rectBreadth, java2dValue1, rectBreadth * (end
                        - start), rectLength * (1 - p));
            }

        }

        if (getShadowsVisible()) {
            getBarPainter().paintBarShadow(g2, this, row, column, bar,
                    barBase, true);
        }
        getBarPainter().paintBar(g2, this, row, column, bar, barBase);

        if (completeBar != null) {
            g2.setPaint(getCompletePaint());
            g2.fill(completeBar);
        }
        if (incompleteBar != null) {
            g2.setPaint(getIncompletePaint());
            g2.fill(incompleteBar);
        }

        // draw the outline...
        if (isDrawBarOutline()
                && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row,
                column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    false);
        }

        // submit the current data point as a crosshair candidate
        int datasetIndex = plot.indexOf(dataset);
        Comparable columnKey = dataset.getColumnKey(column);
        Comparable rowKey = dataset.getRowKey(row);
        double xx = domainAxis.getCategorySeriesMiddle(columnKey, rowKey,
                dataset, getItemMargin(), dataArea, plot.getDomainAxisEdge());
        updateCrosshairValues(state.getCrosshairState(),
                dataset.getRowKey(row), dataset.getColumnKey(column),
                value1.doubleValue(), datasetIndex, xx, java2dValue1,
                orientation);

        // collect entity and tool tip information...
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }