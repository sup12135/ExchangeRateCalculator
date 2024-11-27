package com.example.exchangecal.front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {

    JButton b1 = new JButton("시작하기");
    JButton b2 = new JButton("종료");
    JLabel title = new JLabel("환율계산기", SwingConstants.CENTER);

    public Home() {
        super("환율계산기");

        // 전체 레이아웃 설정
        this.setLayout(new BorderLayout());



        // 중앙 패널에 제목과 버튼 배치
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 수직 배치
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100)); // 상하좌우 여백 설정

        // 제목 설정
        title.setFont(new Font("맑은 고딕", Font.BOLD, 25)); // 제목 폰트 설정
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // 제목 중앙 정렬
        mainPanel.add(title);

        // 제목과 버튼 1 사이 간격 추가
        mainPanel.add(Box.createVerticalStrut(20));

        // 버튼 1 설정 (크기 키움)
        b1.setAlignmentX(Component.CENTER_ALIGNMENT);
        b1.setPreferredSize(new Dimension(320, 120)); // 크기 설정
        b1.setFont(new Font("맑은 고딕", Font.PLAIN, 18)); // 버튼 글씨 크기 증가
        mainPanel.add(b1);

        // 버튼 1과 버튼 2 사이 간격 추가
        mainPanel.add(Box.createVerticalStrut(50));

        // 버튼 2 설정 (크기 키움)
        b2.setAlignmentX(Component.CENTER_ALIGNMENT);
        b2.setPreferredSize(new Dimension(150, 50)); // 크기 설정
        b2.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 버튼 글씨 크기 증가
        mainPanel.add(b2);

        // 중앙 패널을 메인 창에 추가
        this.add(mainPanel, BorderLayout.CENTER);

        // "시작하기" 버튼 클릭 이벤트 추가
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainPage(); // MainPage 창 열기
                dispose(); // Home 창 닫기
            }
        });

        // 종료 버튼에 ActionListener 추가
        b2.addActionListener(e -> System.exit(0));

        // 창 크기 설정 및 중앙에 위치시키기
        this.setSize(500, 500); // 창 크기 설정
        this.setLocationRelativeTo(null); // 화면 중앙에 창 배치
        this.setResizable(false); // 창 크기 변경 불가능하도록 설정

        // 닫기 동작 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 창을 보이도록 설정
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Home();
    }
}
