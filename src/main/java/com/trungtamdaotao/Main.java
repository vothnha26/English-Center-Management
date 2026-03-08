package com.trungtamdaotao;


import javax.swing.SwingUtilities;
import com.trungtamdaotao.view.system.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}