package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.system.PersonnelController;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.AuthContext;
import com.trungtamdaotao.util.security.UserPermission;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PersonnelManagementFrame extends JFrame {

    private final PersonnelController controller;
    private JTable tblPersonnel;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPhone, txtEmail, txtSpecialty;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    public PersonnelManagementFrame() {
        this.controller = new PersonnelController();
        initComponents();
        loadData();
        applySecurity(); // Áp dụng phân quyền ngay khi mở

        this.setTitle("Quản Lý Nhân Sự & Tài Khoản - MIS Center");
        this.setSize(1100, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));

        // --- 1. Form nhập liệu (Bên trái) ---
        JPanel pnlInput = new JPanel(new GridLayout(6, 2, 5, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        pnlInput.add(new JLabel("Họ tên:"));
        txtName = new JTextField(); pnlInput.add(txtName);

        pnlInput.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField(); pnlInput.add(txtPhone);

        pnlInput.add(new JLabel("Email:"));
        txtEmail = new JTextField(); pnlInput.add(txtEmail);

        pnlInput.add(new JLabel("Chuyên môn (IELTS/TOEIC):"));
        txtSpecialty = new JTextField(); pnlInput.add(txtSpecialty);

        // --- 2. Các nút chức năng (Bên dưới form) ---
        JPanel pnlButtons = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm & Cấp TK");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Cho nghỉ việc");
        btnRefresh = new JButton("Làm mới");

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnRefresh);

        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.add(pnlInput, BorderLayout.NORTH);
        pnlLeft.add(pnlButtons, BorderLayout.CENTER);

        // --- 3. Bảng hiển thị (Bên phải) ---
        String[] cols = {"ID", "Họ Tên", "SĐT", "Email", "Chuyên Môn", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0);
        tblPersonnel = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblPersonnel);

        // --- 4. Sự kiện ---
        btnAdd.addActionListener(e -> handleAddPersonnel());
        btnDelete.addActionListener(e -> handleTerminate());
        btnRefresh.addActionListener(e -> loadData());

        this.add(pnlLeft, BorderLayout.WEST);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * PHẦN QUAN TRỌNG: Áp dụng SOLID RBAC
     */
    private void applySecurity() {
        UserPermission p = AuthContext.getPermission();

        // Vô hiệu hóa các nút dựa trên quyền thay vì if-else
        btnDelete.setEnabled(p.canAccessStaffManager());
        btnAdd.setEnabled(p.canAccessStaffManager());
        // Nếu là Giáo viên, chỉ được xem danh sách, không được sửa
        if (!p.canAccessStaffManager()) {
            txtName.setEditable(false);
            txtPhone.setEditable(false);
            // ... chặn các ô khác
        }
    }

    private void handleAddPersonnel() {
        Teacher t = new Teacher();
        t.setFullName(txtName.getText());
        t.setPhone(txtPhone.getText());
        t.setEmail(txtEmail.getText());
        t.setSpecialty(txtSpecialty.getText());

        // Gọi Controller thực hiện luồng: Lưu Teacher -> Tạo Account
        controller.addNewTeacherWithAccount(t);
        loadData();
        JOptionPane.showMessageDialog(this, "Đã thêm nhân sự và tự động cấp tài khoản (Pass mặc định: 123456)");
    }

    private void handleTerminate() {
        int row = tblPersonnel.getSelectedRow();
        if (row != -1) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            controller.terminateTeacher(id);
            loadData();
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Teacher> list = controller.getAllTeachers();
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                    t.getTeacher_id(), t.getFullName(), t.getPhone(),
                    t.getEmail(), t.getSpecialty(), t.getStatus().name()
            });
        }
    }
}