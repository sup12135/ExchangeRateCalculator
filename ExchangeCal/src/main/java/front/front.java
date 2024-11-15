package front;

import javax.swing.JFrame;

public class front extends JFrame {
    public static void main(String[] args) {
        new front();
    }

    public front() {
        super("창 띄우기 예제");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}