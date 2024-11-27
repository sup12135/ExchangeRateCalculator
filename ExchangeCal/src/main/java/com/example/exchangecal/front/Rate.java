package com.example.exchangecal.front;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Rate extends JFrame {
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;

    public Rate() {
        // 창 제목 설정
        super("나라별 환율 그래프");

        // 기본 레이아웃 설정
        this.setLayout(new BorderLayout());

        // 드롭다운 메뉴 생성
        String[][] tablesAndCurrencies = {
                {"wonDaller", "달러"},
                {"wonEuro", "유로"},
                {"wonWian", "위안"},
                {"wonYen", "엔"}
        };

        String[] currencyOptions = {"달러", "유로", "위안", "엔"};
        JComboBox<String> currencySelector = new JComboBox<>(currencyOptions);
        currencySelector.setSelectedItem("달러");
        currencySelector.addActionListener(e -> {
            String selectedCurrency = (String) currencySelector.getSelectedItem();
            String tableName = getTableName(tablesAndCurrencies, selectedCurrency);
            updateChart(tableName, selectedCurrency);
        });

        // 기본 데이터셋 및 그래프 생성
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = createStyledChart("달러"); // createStyledChart 메서드로 변경
        chartPanel = new ChartPanel(chart); // ChartPanel 초기화
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // 초기 그래프 데이터 설정
        updateChart("wonDaller", "달러"); // 초기 데이터 로드

        // UI 구성
        this.add(currencySelector, BorderLayout.NORTH); // 드롭다운을 북쪽에 추가
        this.add(chartPanel, BorderLayout.CENTER); // 차트를 중앙에 추가

        // 뒤로가기 버튼 추가
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            new MainPage(); // Home 창 열기
            dispose(); // Rate 창 닫기
        });
        this.add(backButton, BorderLayout.SOUTH); // 뒤로가기 버튼을 남쪽에 추가

        // JFrame 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setResizable(false);
        this.setVisible(true);
    }

    private String getTableName(String[][] tablesAndCurrencies, String selectedCurrency) {
        for (String[] tableAndCurrency : tablesAndCurrencies) {
            if (tableAndCurrency[1].equals(selectedCurrency)) {
                return tableAndCurrency[0];
            }
        }
        return null;
    }

    private void updateChart(String tableName, String currencyName) {
        dataset.clear();
        String url = "jdbc:mysql://localhost:3306/exchange_db";
        String user = "root";
        String password = "merk";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT date, rate FROM " + tableName + " ORDER BY date ASC"
             )) {

            while (resultSet.next()) {
                String date = resultSet.getString("date");
                double rate = resultSet.getDouble("rate");
                dataset.addValue(rate, currencyName, date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 그래프 업데이트
        if (chartPanel != null) {
            JFreeChart chart = createStyledChart(currencyName); // 스타일이 적용된 차트 생성
            chartPanel.setChart(chart); // ChartPanel 업데이트
        }
    }

    private JFreeChart createStyledChart(String currencyName) {
        JFreeChart chart = ChartFactory.createLineChart(
                currencyName + " 환율 변동 그래프",
                "날짜",
                "환율",
                dataset,
                PlotOrientation.VERTICAL,
                true, // 범례 표시 여부
                true, // 툴팁 표시 여부
                false // URL 생성 여부
        );

        // 그래프 스타일 설정
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); // 라인 색상 설정

        // 툴팁 설정
        renderer.setDefaultToolTipGenerator((dataset, row, column) -> {
            String date = (String) dataset.getColumnKey(column);
            Number rate = dataset.getValue(row, column);
            return String.format("%s %s원", date, rate);
        });

        plot.setRenderer(renderer);

        return chart;
    }

    public static void main(String[] args) {
        new Rate();
    }
}
