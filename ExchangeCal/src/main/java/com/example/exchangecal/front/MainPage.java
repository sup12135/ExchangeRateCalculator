package com.example.exchangecal.front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.json.JSONObject;

public class MainPage extends JFrame {

    private ExchangeAPI exchangeAPI;
    private JComboBox<String> fromComboBox;
    private JTextField amountField;
    private JComboBox<String> toComboBox;
    private JTextField resultField;

    public MainPage() {
        // 창 제목 설정
        super("환율 계산기");

        // 전체 레이아웃 설정 (GridBagLayout 사용)
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 상단 버튼 추가
        JButton graphButton = new JButton("나라별 환율그래프 확인하기");
        graphButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // 버튼이 한 줄 전체 차지
        this.add(graphButton, gbc);

        // 기준 국가 (단위) 라벨과 드롭다운 메뉴
        JLabel fromLabel = new JLabel("기준 국가(단위):");
        fromLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(fromLabel, gbc);

        fromComboBox = new JComboBox<>(new String[]{"USD", "EUR", "KRW", "JPY"});
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(fromComboBox, gbc);

        // 금액 라벨과 텍스트 필드
        JLabel amountLabel = new JLabel("금액:");
        amountLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(amountLabel, gbc);

        amountField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        this.add(amountField, gbc);

        // "=" 기호
        JLabel equalLabel = new JLabel("=", SwingConstants.CENTER);
        equalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        this.add(equalLabel, gbc);

        // 환전할 통화 (단위) 라벨과 드롭다운 메뉴
        JLabel toLabel = new JLabel("환전할 통화(단위):");
        toLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        this.add(toLabel, gbc);

        toComboBox = new JComboBox<>(new String[]{"USD", "EUR", "KRW", "JPY"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        this.add(toComboBox, gbc);

        // 환전된 금액 라벨과 텍스트 필드
        JLabel resultLabel = new JLabel("금액:");
        resultLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        this.add(resultLabel, gbc);

        resultField = new JTextField(10);
        resultField.setEditable(false); // 결과 필드는 수정 불가
        gbc.gridx = 1;
        gbc.gridy = 5;
        this.add(resultField, gbc);

        // 환전 버튼 추가
        JButton convertButton = new JButton("환전하기");
        convertButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        this.add(convertButton, gbc);

        // 환전 버튼 클릭 이벤트 추가
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        // 그래프 버튼 클릭 이벤트 추가
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Rate(); // Rate 페이지 열기
                dispose(); // MainPage 닫기
            }
        });

        exchangeAPI = new ExchangeAPI();

        // 창 크기 설정 및 화면 중앙 배치
        this.setSize(400, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void performConversion() {
        String fromCurrency = (String) fromComboBox.getSelectedItem();
        String toCurrency = (String) toComboBox.getSelectedItem();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "유효한 금액을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                JSONObject exchangeRates = exchangeAPI.getExchangeRate(fromCurrency);
                double rate = exchangeRates.getJSONObject("rates").getDouble(toCurrency);
                double convertedAmount = amount * rate;

                SwingUtilities.invokeLater(() -> resultField.setText(String.format("%.2f", convertedAmount)));
            } catch (IOException | InterruptedException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "환율 정보를 가져오는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    public static void main(String[] args) {
        new MainPage();
    }
}