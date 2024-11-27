package com.example.financialadvisor;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class FinancialAdvisor extends JFrame {

    private JLabel exchangeRateLabel;
    private JLabel kospiLabel;
    private JLabel kosdaqLabel;
    private JLabel adviceLabel;

    public FinancialAdvisor() {
        // 창 제목 설정
        super("Financial Advisor");

        // 기본 레이아웃 설정
        this.setLayout(new BorderLayout());

        // 데이터 표시 패널
        JPanel dataPanel = new JPanel(new GridLayout(4, 1));
        exchangeRateLabel = new JLabel("환율: ", SwingConstants.LEFT);
        kospiLabel = new JLabel("코스피: ", SwingConstants.LEFT);
        kosdaqLabel = new JLabel("코스닥: ", SwingConstants.LEFT);
        adviceLabel = new JLabel("조언: ", SwingConstants.LEFT);
        adviceLabel.setForeground(Color.RED);

        dataPanel.add(exchangeRateLabel);
        dataPanel.add(kospiLabel);
        dataPanel.add(kosdaqLabel);
        dataPanel.add(adviceLabel);

        // 업데이트 버튼
        JButton updateButton = new JButton("데이터 업데이트");
        updateButton.addActionListener(e -> updateData());

        // UI 구성
        this.add(dataPanel, BorderLayout.CENTER);
        this.add(updateButton, BorderLayout.SOUTH);

        // JFrame 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // 초기 데이터 로드
        updateData();
    }

    private void updateData() {
        String url = "jdbc:mysql://localhost:3306/financial_db";
        String user = "root";
        String password = "merk";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            // 환율 데이터 가져오기
            ResultSet exchangeResult = statement.executeQuery("SELECT usd_krw FROM exchange_rate ORDER BY date DESC LIMIT 1");
            double exchangeRate = 0;
            if (exchangeResult.next()) {
                exchangeRate = exchangeResult.getDouble("usd_krw");
                exchangeRateLabel.setText("환율: " + exchangeRate + "원");
            }

            // 코스피 데이터 가져오기
            ResultSet kospiResult = statement.executeQuery("SELECT kospi FROM stock_data ORDER BY date DESC LIMIT 1");
            double kospi = 0;
            if (kospiResult.next()) {
                kospi = kospiResult.getDouble("kospi");
                kospiLabel.setText("코스피: " + kospi);
            }

            // 코스닥 데이터 가져오기
            ResultSet kosdaqResult = statement.executeQuery("SELECT kosdaq FROM stock_data ORDER BY date DESC LIMIT 1");
            double kosdaq = 0;
            if (kosdaqResult.next()) {
                kosdaq = kosdaqResult.getDouble("kosdaq");
                kosdaqLabel.setText("코스닥: " + kosdaq);
            }

            // 분석 및 조언 제공
            String advice = analyzeData(exchangeRate, kospi, kosdaq);
            adviceLabel.setText("조언: " + advice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String analyzeData(double exchangeRate, double kospi, double kosdaq) {
        // 임계값 설정
        double thresholdExchangeRate = 1300.0;

        // 분석 로직
        if (exchangeRate > thresholdExchangeRate) {
            if (kospi < 2000 || kosdaq < 800) {
                return "환율 상승으로 주식 하락 가능성 높음! 보수적으로 투자하세요.";
            } else {
                return "환율 상승 중이지만 주식은 안정적입니다. 신중히 투자하세요.";
            }
        } else {
            return "환율 안정 상태, 투자 기회로 적합합니다.";
        }
    }

    public static void main(String[] args) {
        new FinancialAdvisor();
    }
}
