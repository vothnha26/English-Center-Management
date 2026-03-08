package com.trungtamdaotao.view.student;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;

/**
 * Màn hình Quản lý Học viên (CRUD).
 * Layout: thanh tìm kiếm trên đầu, bảng danh sách ở giữa, form nhập liệu bên dưới.
 */
public class StudentManagerFrame extends JFrame {

    private final StudentController controller;

    // Bảng danh sách
    private JTable table;
    private DefaultTableModel tableModel;

    // Ô tìm kiếm
    private JTextField txtSearch;

    // Form nhập liệu
    private JTextField txtId, txtName, txtPhone, txtEmail, txtAddress, txtDob;

    // Nút hành động
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnEnroll;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMNS = {"ID", "Họ tên", "Ngày sinh", "Giới tính",
                                              "Điện thoại", "Email", "Trạng thái"};

    public StudentManagerFrame() {
        this.controller = new StudentController();
        initUI();
        loadTable(controller.getAllStudents());
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Quản lý Học viên");
        setSize(960, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildFormPanel(),   BorderLayout.SOUTH);
    }

    // ── Top: thanh tìm kiếm ──────────────────────────────────────────────────
    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        txtSearch = new JTextField(24);
        btnSearch = new JButton("Tìm");
        JButton btnReload = new JButton("Tải lại");

        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());
        btnReload.addActionListener(e -> loadTable(controller.getAllStudents()));

        p.add(new JLabel("Tên / SĐT:"));
        p.add(txtSearch);
        p.add(btnSearch);
        p.add(btnReload);
        return p;
    }

    // ── Center: bảng JTable ──────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        return new JScrollPane(table);
    }

    // ── South: form nhập liệu + nút ──────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(4, 4));
        wrapper.setBorder(BorderFactory.createTitledBorder("Thông tin học viên"));

        // Lưới nhập liệu
        JPanel grid = new JPanel(new GridLayout(2, 6, 6, 4));
        txtId      = new JTextField(); txtId.setEditable(false);
        txtName    = new JTextField();
        txtPhone   = new JTextField();
        txtEmail   = new JTextField();
        txtAddress = new JTextField();
        txtDob     = new JTextField("dd/MM/yyyy");

        grid.add(label("ID:")); grid.add(txtId);
        grid.add(label("Họ tên (*):"));   grid.add(txtName);
        grid.add(label("Điện thoại (*):")); grid.add(txtPhone);
        grid.add(label("Email:"));         grid.add(txtEmail);
        grid.add(label("Địa chỉ:"));      grid.add(txtAddress);
        grid.add(label("Ngày sinh:"));     grid.add(txtDob);

        // Nút hành động
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        btnAdd    = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏ Cập nhật");
        btnDelete = new JButton("🗑 Xóa (Inactive)");
        btnClear  = new JButton("⬜ Xóa form");
        btnEnroll = new JButton("📋 Ghi danh lớp học");

        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e  -> clearForm());
        btnEnroll.addActionListener(e -> openEnrollDialog());

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnEnroll);
        btnPanel.add(btnClear);

        wrapper.add(grid, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Nạp dữ liệu vào bảng
    private void loadTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                s.getStudent_id(),
                s.getFullName(),
                s.getDateOfBirth() != null ? s.getDateOfBirth().format(DATE_FMT) : "",
                s.getGender(),
                s.getPhone(),
                s.getEmail(),
                s.getStatus()
            });
        }
    }

    // Chọn hàng → điền vào form
    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        Object dob = tableModel.getValueAt(row, 2);
        txtDob.setText(dob != null ? dob.toString() : "");
        txtPhone.setText(tableModel.getValueAt(row, 4).toString());
        Object email = tableModel.getValueAt(row, 5);
        txtEmail.setText(email != null ? email.toString() : "");
    }

    // ── Hành động CRUD ───────────────────────────────────────────────────────

    private void doSearch() {
        loadTable(controller.searchStudents(txtSearch.getText()));
    }

    private void doAdd() {
        try {
            LocalDate dob = parseDob();
            controller.addStudent(txtName.getText(), txtPhone.getText(),
                                  txtEmail.getText(), txtAddress.getText(), dob);
            JOptionPane.showMessageDialog(this, "Thêm học viên thành công!");
            clearForm();
            loadTable(controller.getAllStudents());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên cần cập nhật.");
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText());
            Student s = controller.getStudentById((long) id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy học viên."); return; }

            s.setFullName(txtName.getText());
            s.setPhone(txtPhone.getText());
            s.setEmail(txtEmail.getText());
            s.setAddress(txtAddress.getText());
            s.setDateOfBirth(parseDob());
            controller.updateStudent(s);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTable(controller.getAllStudents());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên muốn xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Học viên sẽ bị đặt Inactive. Tiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteStudent((long) Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Đã đặt trạng thái Inactive.");
            clearForm();
            loadTable(controller.getAllStudents());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEnrollDialog() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trước.");
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        Student student = controller.getStudentById((long)id);
        if (student != null) {
            new EnrollmentDialog(this, student, controller).setVisible(true);
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText(""); txtDob.setText("dd/MM/yyyy");
        table.clearSelection();
    }

    private LocalDate parseDob() {
        String raw = txtDob.getText().trim();
        if (raw.isEmpty() || raw.equals("dd/MM/yyyy")) return null;
        try { return LocalDate.parse(raw, DATE_FMT); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Ngày sinh sai định dạng dd/MM/yyyy"); }
    }

    private JLabel label(String text) { return new JLabel(text); }

    // ── Entry point (standalone test) ────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerFrame().setVisible(true));
    }
}
