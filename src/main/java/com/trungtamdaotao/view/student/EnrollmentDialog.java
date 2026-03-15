package com.trungtamdaotao.view.student;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

/**
 * Dialog ghi danh học viên vào lớp học.
 * Hiển thị danh sách lớp có sẵn (dropdown) và lịch sử ghi danh.
 */
public class EnrollmentDialog extends JDialog {

    private final Student student;
    private final StudentController controller;

    private JComboBox<ClassEntity> cmbClass;
    private JTable tblEnrollments;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS = {"ID", "Lớp", "Ngày ghi danh", "Trạng thái"};

    public EnrollmentDialog(Window owner, Student student, StudentController controller) {
        super(owner, "Ghi danh: " + student.getFullName(), ModalityType.APPLICATION_MODAL);
        this.student    = student;
        this.controller = controller;
        initUI();
        loadClasses();
        loadEnrollments();
        setSize(600, 440);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));

        // --- Top: chọn lớp + nút ghi danh ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        top.setBorder(BorderFactory.createTitledBorder("Đăng ký lớp mới"));
        cmbClass = new JComboBox<>();
        JButton btnEnroll = new JButton("✅ Ghi danh");
        btnEnroll.addActionListener(e -> doEnroll());
        top.add(new JLabel("Chọn lớp:"));
        top.add(cmbClass);
        top.add(btnEnroll);

        // --- Center: lịch sử ghi danh ---
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEnrollments = new JTable(tableModel);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Lịch sử ghi danh"));
        center.add(new JScrollPane(tblEnrollments));

        // --- Bottom: nút huỷ ghi danh ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("❌ Huỷ ghi danh đã chọn");
        JButton btnClose  = new JButton("Đóng");
        btnCancel.addActionListener(e -> doCancelEnrollment());
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnCancel);
        bottom.add(btnClose);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // Nạp danh sách lớp học vào ComboBox
    private void loadClasses() {
        cmbClass.removeAllItems();
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            List<ClassEntity> classes = em.createQuery(
                "SELECT c FROM ClassEntity c WHERE c.status <> 'Closed'", ClassEntity.class
            ).getResultList();
            for (ClassEntity c : classes) cmbClass.addItem(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được danh sách lớp: " + ex.getMessage());
        }
    }

    // Nạp lịch sử ghi danh của học viên
    private void loadEnrollments() {
        tableModel.setRowCount(0);
        List<Enrollment> list = controller.getEnrollmentsByStudent(student.getStudent_id());
        for (Enrollment e : list) {
            tableModel.addRow(new Object[]{
                e.getEnrollmentId(),
                e.getClazz().getClassName(),
                e.getEnrollmentDate(),
                e.getStatus()
            });
        }
    }

    private void doEnroll() {
        ClassEntity selected = (ClassEntity) cmbClass.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp."); return; }
        try {
            controller.enroll(student, selected);
            JOptionPane.showMessageDialog(this, "Ghi danh thành công!");
            loadEnrollments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doCancelEnrollment() {
        int row = tblEnrollments.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần huỷ."); return; }
        Long id = (Long) ((long) tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this, "Huỷ ghi danh này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.cancelEnrollment(id);
            JOptionPane.showMessageDialog(this, "Đã huỷ ghi danh.");
            loadEnrollments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
