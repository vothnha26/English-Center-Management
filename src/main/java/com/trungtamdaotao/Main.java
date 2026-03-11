package com.trungtamdaotao;

import com.formdev.flatlaf.FlatLightLaf;
import com.trungtamdaotao.util.UIHelper;
import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import com.trungtamdaotao.view.system.LoginFrame;

public class Main {
    public static void main(String[] args) throws Exception {
        // Đảm bảo console in đúng UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Cấu hình giao diện phẳng hiện đại
        FlatLightLaf.setup();

        // Áp dụng font hỗ trợ tiếng Việt toàn cục cho toàn bộ Swing components
        UIHelper.applyGlobalFont();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}