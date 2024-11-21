package front;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Front extends JFrame {
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Front());
    }

    public Front() {
        super("환율 그래프");

        // UI 설정
        this.setLayout(new BorderLayout());

        // 드롭다운 메뉴 생성
        String[][] tablesAndCurrencies = {
                {"wonDaller", "달러"},
                {"wonEuro", "유로"},
                {"wonPound", "파운드"},
                {"wonWian", "위안"},
                {"wonYen", "엔"}
        };

        String[] currencyOptions = {"달러", "유로", "파운드", "위안", "엔"};
        JComboBox<String> currencySelector = new JComboBox<>(currencyOptions);
        currencySelector.addActionListener(e -> {
            String selectedCurrency = (String) currencySelector.getSelectedItem();
            String tableName = getTableName(tablesAndCurrencies, selectedCurrency);
            updateChart(tableName, selectedCurrency);
        });

        // 기본 데이터셋 및 그래프 생성
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = createChart("달러", "wonDaller"); // 초기 차트 생성
        chartPanel = new ChartPanel(chart); // ChartPanel 초기화
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // UI 구성
        this.add(currencySelector, BorderLayout.NORTH);
        this.add(chartPanel, BorderLayout.CENTER);

        // JFrame 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
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
            JFreeChart chart = ChartFactory.createLineChart(
                    currencyName + " 환율 변동 그래프",
                    "날짜",
                    "환율",
                    dataset
            );
            chartPanel.setChart(chart); // ChartPanel 업데이트
        }
    }

    private JFreeChart createChart(String currencyName, String tableName) {
        updateChart(tableName, currencyName); // 초기 데이터 로드
        return ChartFactory.createLineChart(
                currencyName + " 환율 변동 그래프",
                "날짜",
                "환율",
                dataset
        );
    }
}
