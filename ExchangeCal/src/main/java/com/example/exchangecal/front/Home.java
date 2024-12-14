package com.example.exchangecal.front;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class Home extends JFrame {

    JButton b1 = new JButton("시작하기");
    JButton b2 = new JButton("종료");
    JLabel title = new JLabel("환율계산기", SwingConstants.CENTER);
    JTextArea resultArea = new JTextArea("환율 정보를 가져오는 중...\n");

    public Home() {
        super("환율계산기");

        // 전체 레이아웃 설정
        this.setLayout(new BorderLayout());

        // 중앙 패널에 제목과 버튼 배치
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 수직 배치
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // 상하좌우 여백 설정

        // 제목 설정
        title.setFont(new Font("맑은 고딕", Font.BOLD, 25)); // 제목 폰트 설정
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // 제목 중앙 정렬
        mainPanel.add(title);

        // 제목과 버튼 간격 추가
        mainPanel.add(Box.createVerticalStrut(20));

        // 버튼 1 설정
        b1.setAlignmentX(Component.CENTER_ALIGNMENT);
        b1.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        mainPanel.add(b1);

        // 버튼 1과 버튼 2 사이 간격 추가
        mainPanel.add(Box.createVerticalStrut(20));

        // 버튼 2 설정
        b2.setAlignmentX(Component.CENTER_ALIGNMENT);
        b2.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        mainPanel.add(b2);

        // 종료 버튼 아래 환율 정보 추가
        mainPanel.add(Box.createVerticalStrut(30));

        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        mainPanel.add(scrollPane);

        // 중앙 패널을 메인 창에 추가
        this.add(mainPanel, BorderLayout.CENTER);

        // "시작하기" 버튼 클릭 이벤트 추가
        b1.addActionListener(e -> {
            new MainPage(); // MainPage 창 열기
            dispose(); // Home 창 닫기
        });

        // 종료 버튼 클릭 이벤트 추가
        b2.addActionListener(e -> System.exit(0));

        // 창 크기 설정 및 중앙에 위치시키기
        this.setSize(500, 500); // 창 크기 설정
        this.setLocationRelativeTo(null); // 화면 중앙에 창 배치
        this.setResizable(false); // 창 크기 변경 불가능하도록 설정

        // 닫기 동작 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 창을 보이도록 설정
        this.setVisible(true);

        // 초기 데이터 로드
        fetchExchangeRates();
    }

    private void fetchExchangeRates() {
        resultArea.setText("환율 정보를 가져오는 중...\n");

        // 새 스레드에서 API 호출 실행
        new Thread(() -> {
            try {
                JSONObject exchangeRates = getExchangeRate("USD"); // 기준 통화는 USD로 설정
                displayExchangeRates(exchangeRates);
            } catch (IOException | InterruptedException ex) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("환율 정보를 가져오는 데 실패했습니다.\n");
                    resultArea.append("오류 메시지: " + ex.getMessage());
                });
            }
        }).start();
    }

    private JSONObject getExchangeRate(String baseCurrency) throws IOException, InterruptedException {
        String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            throw new IOException("Failed to fetch exchange rates: " + response.statusCode());
        }
    }

    private void displayExchangeRates(JSONObject exchangeRates) {
        SwingUtilities.invokeLater(() -> {
            resultArea.setText(""); // 결과 초기화
            resultArea.append(exchangeRates.getString("date") + " 기준 환율 정보\n\n");

            JSONObject rates = exchangeRates.getJSONObject("rates");
            String[] targetCurrencies = {"KRW", "EUR", "CNY", "JPY"}; // 조회할 통화 목록

            for (String currency : targetCurrencies) {
                if (rates.has(currency)) {
                    double rate = rates.getDouble(currency);
                    resultArea.append("1 USD : " + rate + " " + currency + "\n");
                }
            }
        });
    }

    public static void main(String[] args) {
        new Home();
    }
}
