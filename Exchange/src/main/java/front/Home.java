package com.example.front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    JButton b1 = new JButton("시작하기");
    JButton b2 = new JButton("종료");

    public Home() {
        super("환율계산기");

        // 레이아웃 설정
        this.setLayout(new FlowLayout());

        // 버튼 추가
        this.add(b1);
        this.add(b2);

        // 종료 버튼에 ActionListener 추가
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 창 크기 설정 및 중앙에 위치시키기
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);

        // 닫기 동작 설정
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 창을 보이도록 설정
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Home();
    }
}
