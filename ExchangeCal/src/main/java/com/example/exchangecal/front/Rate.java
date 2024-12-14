package com.example.exchangecal.front;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Rate extends JFrame {
    private XYSeriesCollection dataset;
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
        dataset = new XYSeriesCollection();
        JFreeChart chart = createStyledChart("달러");
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // 초기 그래프 데이터 설정
        updateChart("wonDaller", "달러");

        // UI 구성
        this.add(currencySelector, BorderLayout.NORTH); // 드롭다운을 북쪽에 추가
        this.add(chartPanel, BorderLayout.CENTER); // 차트를 중앙에 추가

        // 뒤로가기 버튼 추가
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            new MainPage(); // MainPage 창 열기
            dispose(); // Rate 창 닫기
        });
        this.add(backButton, BorderLayout.SOUTH); // 뒤로가기 버튼을 남쪽에 추가

        // JFrame 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setResizable(false);
        this.setVisible(true);

        // 하루 한 번 자동 업데이트 설정
        scheduleDailyUpdate();
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
        dataset.removeAllSeries();
        String url = "jdbc:mysql://localhost:3306/exchange_db";
        String user = "root";
        String password = "merk";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT date, rate FROM " + tableName + " ORDER BY date ASC"
             )) {

            XYSeries series = new XYSeries(currencyName);

            while (resultSet.next()) {
                String date = resultSet.getString("date"); // 전체 날짜 저장
                double rate = resultSet.getDouble("rate");
                int year = Integer.parseInt(date.split("-")[0]); // 연도를 숫자로 변환
                series.add(year, rate); // 연도와 환율 추가
            }

            dataset.addSeries(series);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 그래프 업데이트
        if (chartPanel != null) {
            JFreeChart chart = createStyledChart(currencyName);
            chartPanel.setChart(chart);
        }
    }

    private JFreeChart createStyledChart(String currencyName) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                currencyName + " 환율 변동 그래프",
                "연도", // x축 제목
                "환율",
                dataset
        );

        // 그래프 스타일 설정
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // 정수로 표시

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); // 라인 색상 설정

        // 툴팁 설정: 연도 표시
        renderer.setDefaultToolTipGenerator((dataset, series, item) -> {
            Number year = dataset.getX(series, item); // 연도
            Number rate = dataset.getY(series, item); // 환율
            return String.format("%s년 %s원", year.intValue(), rate);
        });

        plot.setRenderer(renderer);

        return chart;
    }

    private void scheduleDailyUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 현재 시간으로부터 자정까지의 시간 계산
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;
        long initialDelay = now.until(midnight, ChronoUnit.SECONDS);
        if (initialDelay <= 0) {
            initialDelay += TimeUnit.DAYS.toSeconds(1); // 다음 날 자정으로 설정
        }

        // 자정 이후 24시간마다 실행
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                updateChart("wonDaller", "달러"); // 기본 통화 업데이트
            });
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        new Rate();
    }
}
