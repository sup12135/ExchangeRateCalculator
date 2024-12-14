package com.example.exchangecal.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class AIModelClient {

    public static String getAdviceFromAI(double usdKrw, double kospi, double kosdaq, double oilPrice, double fedRate, double inflationRate) {
        String apiUrl = "http://localhost:5001/predict"; // Python Flask API URL
        String jsonInput = String.format(
            "{\"USD_KRW\": %f, \"KOSPI\": %f, \"KOSDAQ\": %f, \"Crude_Oil_Price\": %f, \"Fed_Funds_Rate\": %f, \"Inflation_Rate\": %f}",
            usdKrw, kospi, kosdaq, oilPrice, fedRate, inflationRate
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Python API 응답: " + response.body());

            // JSON 응답에서 advice 필드 추출
            JSONObject jsonResponse = new JSONObject(response.body());
            String advice = jsonResponse.getString("advice");

            // Unicode 이스케이프 문자를 디코딩
            return decodeUnicode(advice);
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 조언을 가져오는 데 실패했습니다.";
        }
    }

    private static String decodeUnicode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i++);
            if (ch == '\\' && i < input.length() && input.charAt(i) == 'u') {
                i++;
                int unicodeValue = Integer.parseInt(input.substring(i, i + 4), 16);
                result.append((char) unicodeValue);
                i += 4;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
