package com.example.exchangecal.front;

import javax.swing.*;
import java.awt.*;

public class Rate extends JFrame {

    public Rate() {
        // 창 제목 설정
        super("나라별 환율 그래프");

        // 기본 레이아웃 설정
        this.setLayout(new BorderLayout());

        // 중앙에 안내문 추가
        JLabel label = new JLabel("나라별 환율 그래프 페이지", SwingConstants.CENTER);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        this.add(label, BorderLayout.CENTER);

        // 창 크기 및 기본 설정
        this.setSize(500, 400);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Rate();
    }
}
