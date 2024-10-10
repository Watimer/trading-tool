package com.wizard.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author wizard
 * @date 2024-09-20
 * @desc
 */


public class AlertImageGenerator {

	public static void main(String[] args) throws IOException {
		String[][] alertData = {
				{"标的", "成交量/市值", "24H涨跌", "波动率", "评级"},
				{"SXP", "1.99", "59.82", "16.46", "225"},
				{"TURBO", "0.49", "18.52", "5.91", "215"},
				{"ALT", "0.67", "18.52", "6.00", "186"},
				{"KAVA", "0.54", "17.97", "5.44", "129"},
				{"TAO", "0.06", "9.16", "6.40", "32"},
				{"SUI", "0.18", "4.50", "6.82", "23"},
				{"DOGS", "0.52", "3.20", "5.89", "110"},
				{"BIGTIME", "0.85", "2.11", "7.97", "260"},
				{"TAIKO", "1.06", "-8.68", "6.32", "274"},
				{"ZETA", "0.75", "-11.26", "5.87", "176"},
				{"SXP", "1.99", "59.82", "16.46", "225"},
				{"TURBO", "0.49", "18.52", "5.91", "215"},
				{"ALT", "0.67", "18.52", "6.00", "186"},
				{"KAVA", "0.54", "17.97", "5.44", "129"},
				{"TAO", "0.06", "9.16", "6.40", "32"},
				{"SUI", "0.18", "4.50", "6.82", "23"},
				{"DOGS", "0.52", "3.20", "5.89", "110"},
				{"BIGTIME", "0.85", "2.11", "7.97", "260"},
				{"TAIKO", "1.06", "-8.68", "6.32", "274"},
				{"ZETA", "0.75", "-11.26", "5.87", "176"}
		};

		String filePath = "/Users/yueyaoli/Documents/中科睿见/本地项目日志/logs/alert_image.png";

		generateImageWithData(alertData, "警报类型: 强势标的", "2024-09-20 11:15:58", filePath);
	}

	public static String generateImageWithData(String[][] data, String title, String time, String outputFileName) throws IOException {
		int imageWidth = 600;
		int rowHeight = 30;
		int colWidth = 100;
		int headerHeight = 40; // 标题高度
		int timeHeight = 30; // 时间高度
		int paddingHeight = 20; // 上下间距

		// 根据数据行数动态计算图片的高度
		int dataHeight = data.length * rowHeight;
		int imageHeight = headerHeight + timeHeight + dataHeight + paddingHeight * 2;

		// 创建一个BufferedImage对象
		BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

		// 获取Graphics2D对象
		Graphics2D g2d = bufferedImage.createGraphics();

		// 设置抗锯齿以提高文字清晰度
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 设置背景为渐变色
		GradientPaint gradientPaint = new GradientPaint(0, 0, Color.CYAN, 0, imageHeight, Color.YELLOW);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(0, 0, imageWidth, imageHeight);

		// 设置字体
		Font headerFont = new Font("Arial", Font.BOLD, 16);
		Font textFont = new Font("Arial", Font.PLAIN, 14);
		g2d.setFont(headerFont);

		// 设置文字颜色
		g2d.setColor(Color.BLACK);

		// 居中显示标题
		Font titleFont = new Font("Arial", Font.BOLD, 20);
		g2d.setFont(titleFont);
		FontMetrics titleFontMetrics = g2d.getFontMetrics(titleFont);
		int titleWidth = titleFontMetrics.stringWidth(title);
		int titleX = (imageWidth - titleWidth) / 2;
		g2d.drawString(title, titleX, paddingHeight + titleFontMetrics.getHeight());

		// 绘制时间，靠右显示
		g2d.setFont(textFont);
		FontMetrics timeFontMetrics = g2d.getFontMetrics(textFont);
		int timeWidth = timeFontMetrics.stringWidth("时间: " + time);
		int timeX = imageWidth - timeWidth - 20; // 靠右并留出20px的边距
		g2d.drawString("时间: " + time, timeX, paddingHeight + headerHeight);

		// 计算表格总宽度，动态居中表格
		int tableWidth = colWidth * data[0].length;
		int startX = (imageWidth - tableWidth) / 2; // 居中显示表格
		int startY = paddingHeight + headerHeight + timeHeight; // 表格开始的Y坐标

		// 绘制表格头，居中
		g2d.setFont(headerFont);
		for (int i = 0; i < data[0].length; i++) {
			String headerText = data[0][i];
			FontMetrics headerFontMetrics = g2d.getFontMetrics(headerFont);
			int headerWidth = headerFontMetrics.stringWidth(headerText);
			int headerX = startX + i * colWidth + (colWidth - headerWidth) / 2; // 居中计算
			g2d.drawString(headerText, headerX, startY);
		}

		// 绘制表格内容，居中
		g2d.setFont(textFont);
		for (int row = 1; row < data.length; row++) {
			for (int col = 0; col < data[row].length; col++) {
				String cellText = data[row][col];
				FontMetrics textFontMetrics = g2d.getFontMetrics(textFont);
				int textWidth = textFontMetrics.stringWidth(cellText);
				int textX = startX + col * colWidth + (colWidth - textWidth) / 2; // 居中计算
				g2d.drawString(cellText, textX, startY + row * rowHeight);
			}
		}

		// 添加水印 "Tradingview"
		String watermarkText = "TradingView";
		g2d.setFont(new Font("Arial", Font.ITALIC, 18));
		g2d.setColor(new Color(255, 255, 255, 128)); // 白色半透明
		FontMetrics watermarkFontMetrics = g2d.getFontMetrics();
		int watermarkWidth = watermarkFontMetrics.stringWidth(watermarkText);
		int watermarkX = imageWidth - watermarkWidth - 10; // 靠右并留出10px的边距
		int watermarkY = imageHeight - 10; // 靠下并留出10px的边距
		g2d.drawString(watermarkText, watermarkX, watermarkY);

		// 释放资源
		g2d.dispose();

		// 释放资源
		g2d.dispose();

		// 将图片保存为文件

		File outputFile = new File(outputFileName);
		ImageIO.write(bufferedImage, "png", outputFile);
		return outputFileName;
	}
}
