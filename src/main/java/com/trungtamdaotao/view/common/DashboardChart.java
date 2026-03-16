package com.trungtamdaotao.view.common;

import com.trungtamdaotao.util.UIHelper;
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
        g2.setColor(new Color(200, 200, 200, 120));
        int x0 = padding + labelPadding;
        int y0 = height - padding;
        int x1 = width - padding;
        int y1 = padding;
        g2.drawLine(x0, y0, x1, y0); // x-axis
        g2.drawLine(x0, y0, x0, y1); // y-axis

        // Y-axis ticks and labels
        int yTicks = 5;
        int maxVal = getMaxDataPoint();
        for (int i = 0; i <= yTicks; i++) {
            int y = y0 - (i * (y0 - y1) / yTicks);
            g2.setColor(new Color(220, 220, 220, 120));
            g2.drawLine(x0 - 4, y, x0 + 4, y);
            String label = String.valueOf((int) Math.round((double) maxVal * i / yTicks));
            g2.setColor(UIHelper.TEXT_COLOR);
            g2.setFont(UIHelper.MAIN_FONT);
            g2.drawString(label, 6, y + 4);
        }

        if (dataPoints == null || dataPoints.isEmpty()) return;

        double xScale = ((double) width - 2 * padding - labelPadding) / (dataPoints.size() - 1);
        double yScale = ((double) height - 2 * padding) / getMaxDataPoint();

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < dataPoints.size(); i++) {
            int gx = (int) (i * xScale + padding + labelPadding);
            int gy = (int) ((getMaxDataPoint() - dataPoints.get(i)) * yScale + padding);
            graphPoints.add(new Point(gx, gy));
        }

        // Vẽ đường nối
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int xA = graphPoints.get(i).x;
            int yA = graphPoints.get(i).y;
            int xB = graphPoints.get(i + 1).x;
            int yB = graphPoints.get(i + 1).y;
            g2.drawLine(xA, yA, xB, yB);
        }

        // Vẽ các điểm tròn và đổ bóng mờ dưới đường
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - 4;
            int y = graphPoints.get(i).y - 4;
            g2.fillOval(x, y, 8, 8);
        }
        // X-axis labels
        g2.setColor(UIHelper.TEXT_COLOR);
        g2.setFont(UIHelper.MAIN_FONT);
        for (int i = 0; i < dataPoints.size(); i++) {
            int x = (int) (i * xScale + padding + labelPadding);
            String xl = String.valueOf(i + 1);
            g2.drawString(xl, x - 6, y0 + 16);
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
