package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.CourseController;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.CourseLevel;
import com.trungtamdaotao.model.entity.enums.DurationUnit;
import com.trungtamdaotao.model.entity.enums.ClassStatus;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Quản lý Học vụ tích hợp Tree View - Đã sửa lỗi Mapping dữ liệu và đầy đủ trường Entity.
 */
public class AcademicManagerPanel extends BaseManagerPanel {
    private final CourseController courseController;
    private final ClassController classController;
    
    private JTree treeAcademic;
    private JPanel pnlDetailContainer;
    private CardLayout detailLayout;

    // --- Course Form Fields ---
    private JTextField txtCourseName, txtCourseFee, txtDuration;
    private JComboBox<CourseLevel> cmbLevel;
    private JComboBox<DurationUnit> cmbDurationUnit;
    private JTextArea txtCourseDesc;

    // --- Class Form Fields ---
    private JTextField txtClassName, txtMaxStudent, txtStartDate, txtEndDate;
    private JComboBox<ClassStatus> cmbClassStatus;

    // --- Buttons ---
    private JButton btnAddCourse, btnUpdateCourse, btnDeleteCourse;
    private JButton btnAddClass, btnUpdateClass, btnDeleteClass;

    private Object selectedObject; // Lưu trữ đối tượng đang được chọn thực tế

    public AcademicManagerPanel() {
        super();
        this.courseController = new CourseController();
        this.classController = new ClassController();
        refreshTree();
    }

    @Override
    protected void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(10);
        splitPane.setOpaque(false);

        // --- LEFT SIDE: TREE ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setOpaque(false);
        pnlLeft.add(new JLabel("CẤU TRÚC ĐÀO TẠO", SwingConstants.CENTER), BorderLayout.NORTH);
        treeAcademic = new JTree();
        treeAcademic.setFont(UIHelper.MAIN_FONT);
        pnlLeft.add(new JScrollPane(treeAcademic), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlLeft);

        // --- RIGHT SIDE: DETAILS ---
        detailLayout = new CardLayout();
        pnlDetailContainer = new JPanel(detailLayout);
        pnlDetailContainer.setBackground(Color.WHITE);

        pnlDetailContainer.add(createWelcomePanel(), "Empty");
        pnlDetailContainer.add(createCourseForm(), "Course");
        pnlDetailContainer.add(createClassForm(), "Class");

        splitPane.setRightComponent(pnlDetailContainer);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createCourseForm() {
        JPanel p = new JPanel(new MigLayout("wrap 2, inset 25, fillx", "[][grow]"));
        p.setOpaque(false);
        p.add(new JLabel("THÔNG TIN KHÓA HỌC"), "span 2, center, gapy 0 20");
        ((JLabel)p.getComponent(0)).setFont(UIHelper.TITLE_FONT);
        ((JLabel)p.getComponent(0)).setForeground(UIHelper.PRIMARY_COLOR);

        p.add(createFieldLabel("Tên khóa học:")); p.add(txtCourseName = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Cấp độ:")); p.add(cmbLevel = new JComboBox<>(CourseLevel.values()), "growx, height 35");
        p.add(createFieldLabel("Thời lượng:")); 
        JPanel pnlDur = new JPanel(new MigLayout("insets 0", "[grow][]")); pnlDur.setOpaque(false);
        pnlDur.add(txtDuration = new JTextField(), "growx, height 35");
        pnlDur.add(cmbDurationUnit = new JComboBox<>(DurationUnit.values()), "height 35");
        p.add(pnlDur, "growx");
        
        p.add(createFieldLabel("Học phí:")); p.add(txtCourseFee = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Mô tả:")); 
        txtCourseDesc = new JTextArea(4, 20);
        p.add(new JScrollPane(txtCourseDesc), "growx");

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtns.setOpaque(false);
        btnAddCourse = UIHelper.createStandardButton("Thêm mới", UIHelper.SUCCESS_COLOR, "➕");
        btnUpdateCourse = UIHelper.createStandardButton("Cập nhật", UIHelper.PRIMARY_COLOR, "📝");
        btnDeleteCourse = UIHelper.createStandardButton("Xóa", Color.RED, "🗑");
        pnlBtns.add(btnAddCourse); pnlBtns.add(btnUpdateCourse); pnlBtns.add(btnDeleteCourse);
        p.add(pnlBtns, "span 2, center, gapy 20");
        
        return p;
    }

    private JPanel createClassForm() {
        JPanel p = new JPanel(new MigLayout("wrap 2, inset 25, fillx", "[][grow]"));
        p.setOpaque(false);
        p.add(new JLabel("THÔNG TIN LỚP HỌC"), "span 2, center, gapy 0 20");
        ((JLabel)p.getComponent(0)).setFont(UIHelper.TITLE_FONT);
        ((JLabel)p.getComponent(0)).setForeground(UIHelper.SECONDARY_COLOR);

        p.add(createFieldLabel("Tên lớp:")); p.add(txtClassName = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Sĩ số tối đa:")); p.add(txtMaxStudent = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Ngày bắt đầu:")); p.add(txtStartDate = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Ngày kết thúc:")); p.add(txtEndDate = new JTextField(), "growx, height 35");
        p.add(createFieldLabel("Trạng thái:")); p.add(cmbClassStatus = new JComboBox<>(ClassStatus.values()), "growx, height 35");

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtns.setOpaque(false);
        btnAddClass = UIHelper.createStandardButton("Thêm mới", UIHelper.SUCCESS_COLOR, "➕");
        btnUpdateClass = UIHelper.createStandardButton("Cập nhật", UIHelper.PRIMARY_COLOR, "📝");
        btnDeleteClass = UIHelper.createStandardButton("Xóa", Color.RED, "🗑");
        pnlBtns.add(btnAddClass); pnlBtns.add(btnUpdateClass); pnlBtns.add(btnDeleteClass);
        p.add(pnlBtns, "span 2, center, gapy 20");
        
        return p;
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout()); p.setOpaque(false);
        p.add(new JLabel("Vui lòng chọn Khóa học hoặc Lớp học trên cây thư mục."));
        return p;
    }

    private void fillCourseForm(Course c) {
        txtCourseName.setText(c.getCourseName());
        cmbLevel.setSelectedItem(c.getLevel());
        txtDuration.setText(c.getDuration() != null ? c.getDuration().toString() : "");
        cmbDurationUnit.setSelectedItem(c.getDurationUnit());
        txtCourseFee.setText(c.getFee().toString());
        txtCourseDesc.setText(c.getDescription());
    }

    private void fillClassForm(ClassEntity cl) {
        txtClassName.setText(cl.getClassName());
        txtMaxStudent.setText(String.valueOf(cl.getMaxStudent()));
        txtStartDate.setText(cl.getStartDate() != null ? cl.getStartDate().toString() : "");
        txtEndDate.setText(cl.getEndDate() != null ? cl.getEndDate().toString() : "");
        cmbClassStatus.setSelectedItem(cl.getStatus());
    }

    @Override
    protected void handleEvents() {
        treeAcademic.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeAcademic.getLastSelectedPathComponent();
            if (node == null) return;
            selectedObject = node.getUserObject();
            if (selectedObject instanceof Course) {
                fillCourseForm((Course) selectedObject);
                detailLayout.show(pnlDetailContainer, "Course");
            } else if (selectedObject instanceof ClassEntity) {
                fillClassForm((ClassEntity) selectedObject);
                detailLayout.show(pnlDetailContainer, "Class");
            } else {
                detailLayout.show(pnlDetailContainer, "Empty");
            }
        });

        btnAddCourse.addActionListener(e -> {
            Course c = new Course();
            updateCourseFromForm(c);
            String msg = courseController.createCourse(c);
            JOptionPane.showMessageDialog(this, msg);
            refreshTree();
        });

        btnUpdateCourse.addActionListener(e -> {
            if (!(selectedObject instanceof Course)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học để cập nhật!");
                return;
            }
            Course c = (Course) selectedObject;
            updateCourseFromForm(c);
            String msg = courseController.updateCourse(c);
            JOptionPane.showMessageDialog(this, msg);
            refreshTree();
        });

        btnDeleteCourse.addActionListener(e -> {
            if (!(selectedObject instanceof Course)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học để xóa!");
                return;
            }
            Course c = (Course) selectedObject;
            int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khóa học này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                String msg = courseController.deleteCourse(c.getCourse_id().intValue());
                JOptionPane.showMessageDialog(this, msg);
                refreshTree();
            }
        });

        btnAddClass.addActionListener(e -> {
            if (!(selectedObject instanceof Course)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học trên cây thư mục để thêm lớp!");
                return;
            }
            ClassEntity cl = new ClassEntity();
            cl.setCourse((Course) selectedObject);
            updateClassFromForm(cl);
            String msg = classController.createClass(cl);
            JOptionPane.showMessageDialog(this, msg);
            refreshTree();
        });

        btnUpdateClass.addActionListener(e -> {
            if (!(selectedObject instanceof ClassEntity)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học để cập nhật!");
                return;
            }
            ClassEntity cl = (ClassEntity) selectedObject;
            updateClassFromForm(cl);
            String msg = classController.updateClass(cl);
            JOptionPane.showMessageDialog(this, msg);
            refreshTree();
        });

        btnDeleteClass.addActionListener(e -> {
            if (!(selectedObject instanceof ClassEntity)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học để xóa!");
                return;
            }
            ClassEntity cl = (ClassEntity) selectedObject;
            int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lớp học này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                String msg = classController.deleteClass(cl.getClass_id().intValue());
                JOptionPane.showMessageDialog(this, msg);
                refreshTree();
            }
        });
    }

    private void updateCourseFromForm(Course c) {
        c.setCourseName(txtCourseName.getText());
        c.setLevel((CourseLevel) cmbLevel.getSelectedItem());
        try {
            c.setDuration(Integer.parseInt(txtDuration.getText()));
        } catch (NumberFormatException e) {
            c.setDuration(0);
        }
        c.setDurationUnit((DurationUnit) cmbDurationUnit.getSelectedItem());
        try {
            c.setFee(new BigDecimal(txtCourseFee.getText()));
        } catch (Exception e) {
            c.setFee(BigDecimal.ZERO);
        }
        c.setDescription(txtCourseDesc.getText());
    }

    private void updateClassFromForm(ClassEntity cl) {
        cl.setClassName(txtClassName.getText());
        try {
            cl.setMaxStudent(Integer.parseInt(txtMaxStudent.getText()));
        } catch (NumberFormatException e) {
            cl.setMaxStudent(0);
        }
        try {
            cl.setStartDate(LocalDate.parse(txtStartDate.getText()));
        } catch (Exception e) {
            cl.setStartDate(null);
        }
        try {
            cl.setEndDate(LocalDate.parse(txtEndDate.getText()));
        } catch (Exception e) {
            cl.setEndDate(null);
        }
        cl.setStatus((ClassStatus) cmbClassStatus.getSelectedItem());
    }

    private void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("TRUNG TÂM ANH NGỮ");
        
        List<Course> courses = courseController.getAllCourses();
        // Sử dụng Java Lambda để xây dựng cây thư mục từ Database
        courses.forEach(c -> {
            DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(c);
            List<ClassEntity> classes = classController.getClassesByCourse(c);
            classes.forEach(cl -> cNode.add(new DefaultMutableTreeNode(cl)));
            root.add(cNode);
        });
        
        treeAcademic.setModel(new DefaultTreeModel(root));
        for (int i = 0; i < treeAcademic.getRowCount(); i++) treeAcademic.expandRow(i);
    }

    @Override protected void loadTableData() {}
}
