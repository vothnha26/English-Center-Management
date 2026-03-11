package com.trungtamdaotao.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Thành phần vẽ biểu đồ tăng trưởng chuyên nghiệp cho Dashboard.
 */
public class DashboardChart extends JPanel {
    private List<Integer> dataPoints;
    private String chartTitle;
    private Color lineColor;

    public DashboardChart(String title, List<Integer> points, Color color) {
        this.chartTitle = title;
        this.dataPoints = points;
        this.lineColor = color;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int labelPadding = 20;

        // Vẽ trục tọa độ mờ
        g2.setColor(new Color(200, 200, 200, 50));
        g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);
        g2.drawLine(padding + labelPadding, padding, padding + labelPadding, height - padding);

        if (dataPoints == null || dataPoints.isEmpty()) return;

        double xScale = ((double) width - 2 * padding - labelPadding) / (dataPoints.size() - 1);
        double yScale = ((double) height - 2 * padding) / getMaxDataPoint();

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < dataPoints.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxDataPoint() - dataPoints.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // Vẽ đường nối
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        // Vẽ các điểm tròn và đổ bóng mờ dưới đường
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - 4;
            int y = graphPoints.get(i).y - 4;
            g2.fillOval(x, y, 8, 8);
        }
        
        // Tiêu đề biểu đồ
        g2.setColor(UIHelper.TEXT_COLOR);
        g2.setFont(UIHelper.BOLD_FONT);
        g2.drawString(chartTitle, padding, padding - 10);
    }

    private int getMaxDataPoint() {
        int max = Integer.MIN_VALUE;
        for (Integer p : dataPoints) max = Math.max(max, p);
        return max == 0 ? 1 : max;
    }
}
