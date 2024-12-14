package com.example.exchangecal.front;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class ExchangeAPI {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    public JSONObject getExchangeRate(String baseCurrency) throws IOException, InterruptedException {
        String requestUrl = API_URL + baseCurrency;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExchangeRateGUI());
    }
}

class ExchangeRateGUI extends JFrame {
    private final JTextArea resultArea;
    private final JComboBox<String> currencySelector;
    private final ExchangeAPI exchangeAPI;

    public ExchangeRateGUI() {
        exchangeAPI = new ExchangeAPI();

        setTitle("환율 조회 프로그램");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널: 기준 통화 선택
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("기준 통화:"));

        String[] baseCurrencyOptions = {"USD", "EUR", "CNY", "JPY"};
        currencySelector = new JComboBox<>(baseCurrencyOptions);
        currencySelector.addActionListener(e -> fetchExchangeRates());
        inputPanel.add(currencySelector);

        add(inputPanel, BorderLayout.NORTH);

        // 중앙 패널: 결과 표시
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        // 초기 데이터 로드
        fetchExchangeRates();

        // 자동 갱신 설정
        scheduleDailyUpdate();
    }

    private void fetchExchangeRates() {
        String baseCurrency = (String) currencySelector.getSelectedItem();

        resultArea.setText("환율 정보를 가져오는 중...\n");

        // 새 스레드에서 API 호출 실행
        new Thread(() -> {
            try {
                JSONObject exchangeRates = exchangeAPI.getExchangeRate(baseCurrency);
                displayExchangeRates(exchangeRates, baseCurrency);
            } catch (IOException | InterruptedException ex) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("환율 정보를 가져오는 데 실패했습니다.\n");
                    resultArea.append("오류 메시지: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void displayExchangeRates(JSONObject exchangeRates, String baseCurrency) {
        SwingUtilities.invokeLater(() -> {
            resultArea.setText(""); // 결과 초기화
            resultArea.append(exchangeRates.getString("date") + "\n");

            String targetCurrency = "KRW"; // 원화 기준
            JSONObject rates = exchangeRates.getJSONObject("rates");

            if (rates.has(targetCurrency)) {
                double rate = rates.getDouble(targetCurrency);
                resultArea.append("1 " + baseCurrency + " : " + rate + " 원\n");
            } else {
                resultArea.append("해당 통화 정보가 없습니다.\n");
            }
        });
    }

    private void scheduleDailyUpdate() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 현재 시간으로부터 자정까지의 시간 계산
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;
        long initialDelay = now.until(midnight, ChronoUnit.SECONDS); // ChronoUnit.SECONDS 사용
        if (initialDelay <= 0) {
            initialDelay += TimeUnit.DAYS.toSeconds(1); // 다음 날 자정으로 설정
        }

        // 자정 이후 24시간마다 실행
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::fetchExchangeRates); // fetchExchangeRates 호출
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
}
