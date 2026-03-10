package com.trungtamdaotao;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import com.trungtamdaotao.view.system.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Cấu hình giao diện phẳng hiện đại
        FlatLightLaf.setup();
        
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}