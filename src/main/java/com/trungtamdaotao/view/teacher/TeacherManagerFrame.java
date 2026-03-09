package com.trungtamdaotao.view.teacher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.teacher.TeacherController;
import com.trungtamdaotao.model.entity.core.Teacher;

/**
 * Màn hình quản lý giáo viên. Cấu trúc tương tự StudentManagerFrame.
 */
public class TeacherManagerFrame extends JFrame {

    private final TeacherController controller;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;

    private JTextField txtId, txtFullName, txtPhone, txtEmail, txtSpecialty, txtHireDate;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMNS = {"ID", "Họ tên", "Điện thoại", "Email",
            "Chuyên môn", "Ngày tuyển", "Trạng thái"};

    public TeacherManagerFrame() {
        this.controller = new TeacherController();
        initUI();
        loadTable(controller.getAllTeachers());
    }

    private void initUI() {
        setTitle("Quản lý Giáo viên");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        txtSearch = new JTextField(24);
        btnSearch = new JButton("Tìm");
        JButton btnReload = new JButton("Tải lại");

        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());
        btnReload.addActionListener(e -> loadTable(controller.getAllTeachers()));

        p.add(new JLabel("Tên / SĐT: "));
        p.add(txtSearch);
        p.add(btnSearch);
        p.add(btnReload);
        return p;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        return new JScrollPane(table);
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(4, 4));
        wrapper.setBorder(BorderFactory.createTitledBorder("Thông tin giáo viên"));

        JPanel grid = new JPanel(new GridLayout(2, 6, 6, 4));
        txtId = new JTextField(); txtId.setEditable(false);
        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtSpecialty = new JTextField();
        txtHireDate = new JTextField("dd/MM/yyyy");

        grid.add(label("ID:")); grid.add(txtId);
        grid.add(label("Họ tên (*):")); grid.add(txtFullName);
        grid.add(label("Điện thoại (*):")); grid.add(txtPhone);
        grid.add(label("Email:")); grid.add(txtEmail);
        grid.add(label("Chuyên môn:")); grid.add(txtSpecialty);
        grid.add(label("Ngày tuyển:")); grid.add(txtHireDate);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        btnAdd = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏ Cập nhật");
        btnDelete = new JButton("🗑 Xóa (Inactive)");
        btnClear = new JButton("⬜ Xóa form");

        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        wrapper.add(grid, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel label(String text) {
        return new JLabel(text);
    }

    private void loadTable(List<Teacher> list) {
        tableModel.setRowCount(0);
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                t.getTeacher_id(),
                t.getFullName(),
                t.getPhone(),
                t.getEmail(),
                t.getSpecialty(),
                t.getHireDate() != null ? t.getHireDate().format(DATE_FMT) : "",
                t.getStatus()
            });
        }
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtFullName.setText(tableModel.getValueAt(row, 1).toString());
        txtPhone.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtSpecialty.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtHireDate.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
    }

    private void doSearch() {
        loadTable(controller.searchTeachers(txtSearch.getText()));
    }

    private LocalDate parseDate(String text) {
        try {
            if (text == null || text.isBlank()) return null;
            return LocalDate.parse(text, DATE_FMT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày không hợp lệ, định dạng dd/MM/yyyy");
        }
    }

    private void doAdd() {
        try {
            LocalDate hire = parseDate(txtHireDate.getText());
            controller.addTeacher(txtFullName.getText(), txtPhone.getText(), txtEmail.getText(),
                    txtSpecialty.getText(), hire);
            JOptionPane.showMessageDialog(this, "Thêm giáo viên thành công!");
            clearForm();
            loadTable(controller.getAllTeachers());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giáo viên cần cập nhật.");
            return;
        }
        try {
            Teacher t = controller.getTeacherById(Long.valueOf(txtId.getText()));
            t.setFullName(txtFullName.getText());
            t.setPhone(txtPhone.getText());
            t.setEmail(txtEmail.getText());
            t.setSpecialty(txtSpecialty.getText());
            t.setHireDate(parseDate(txtHireDate.getText()));
            controller.updateTeacher(t);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadTable(controller.getAllTeachers());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giáo viên cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Chắc chắn muốn đánh inactive giáo viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTeacher(Long.valueOf(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Đã đặt trạng thái Inactive.");
            clearForm();
            loadTable(controller.getAllTeachers());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtSpecialty.setText("");
        txtHireDate.setText("dd/MM/yyyy");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TeacherManagerFrame().setVisible(true));
    }
}
