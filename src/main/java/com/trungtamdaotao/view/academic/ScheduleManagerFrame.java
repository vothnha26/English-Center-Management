package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.ScheduleController;
import com.trungtamdaotao.model.dao.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Quản lý lịch học với giao diện Thời khóa biểu trực quan (Visual Calendar) cho Giáo viên
 * và Bảng danh sách cho Nhân sự.
 */
public class ScheduleManagerFrame extends BaseManagerFrame {
    private final ScheduleController scheduleController;
    private final ClassController classController;
    private final IRoomDAO roomDAO;
    
    private JTabbedPane tabbedPane;
    private JPanel pnlCalendarGrid;
    private JTable tblSchedule;
    private DefaultTableModel tableModel;
    
    // Form fields (Dành cho chức năng chỉnh sửa)
    private JTextField txtStudyDate, txtStartTime, txtEndTime, txtSearchDate;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<Room> cmbRoom;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnReload;
    
    private Schedule selectedSchedule;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleManagerFrame() {
        super("THỜI KHÓA BIỂU TRỰC QUAN");
        this.scheduleController = new ScheduleController();
        this.classController = new ClassController();
        this.roomDAO = new RoomDAOImpl();
        
        loadComboBoxData();
        loadTableData(); // Sẽ cập nhật cả Table và Calendar Grid
    }

    @Override
    protected void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        // --- TAB 1: VISUAL CALENDAR (Dành cho Giáo viên) ---
        tabbedPane.addTab("📅 LỊCH DẠY TRONG TUẦN", createCalendarTab());

        // --- TAB 2: DATA MANAGEMENT (Dành cho Nhân sự) ---
        tabbedPane.addTab("📋 QUẢN LÝ DỮ LIỆU", createManagementTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCalendarTab() {
        JPanel pnlCalendar = new JPanel(new BorderLayout());
        pnlCalendar.setOpaque(false);

        // Header của lịch (Thứ 2 -> CN)
        JPanel pnlDaysHeader = new JPanel(new GridLayout(1, 7, 5, 0));
        pnlDaysHeader.setBackground(UIHelper.ACCENT_COLOR);
        String[] days = {"THỨ 2", "THỨ 3", "THỨ 4", "THỨ 5", "THỨ 6", "THỨ 7", "CHỦ NHẬT"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(UIHelper.BOLD_FONT);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
            pnlDaysHeader.add(lbl);
        }
        pnlCalendar.add(pnlDaysHeader, BorderLayout.NORTH);

        // Grid chứa các thẻ lịch
        pnlCalendarGrid = new JPanel(new GridLayout(1, 7, 5, 5));
        pnlCalendarGrid.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlCalendarGrid.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        pnlCalendar.add(new JScrollPane(pnlCalendarGrid), BorderLayout.CENTER);
        return pnlCalendar;
    }

    private JPanel createManagementTab() {
        JPanel pnlMgmt = new JPanel(new BorderLayout());
        
        // Form chỉnh sửa bên trái (Master-Detail style)
        JPanel pnlForm = new JPanel(new MigLayout("wrap 1, inset 20, fillx", "[fill]", "[]10[]20[]10[]10[]10[]10[]10[]20[]"));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setPreferredSize(new Dimension(350, 0));
        pnlForm.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        pnlForm.add(new JLabel("THÔNG TIN LỊCH HỌC"), "center, gapy 10 20");
        ((JLabel)pnlForm.getComponent(0)).setFont(UIHelper.TITLE_FONT);
        ((JLabel)pnlForm.getComponent(0)).setForeground(UIHelper.PRIMARY_COLOR);

        pnlForm.add(createFieldLabel("Lớp học:"));
        cmbClass = new JComboBox<>();
        pnlForm.add(cmbClass, "height 35");

        pnlForm.add(createFieldLabel("Ngày học (yyyy-mm-dd):"));
        pnlForm.add(txtStudyDate = new JTextField(), "height 35");

        pnlForm.add(createFieldLabel("Giờ bắt đầu:"));
        pnlForm.add(txtStartTime = new JTextField(), "height 35");

        pnlForm.add(createFieldLabel("Giờ kết thúc:"));
        pnlForm.add(txtEndTime = new JTextField(), "height 35");

        pnlForm.add(createFieldLabel("Phòng học:"));
        cmbRoom = new JComboBox<>();
        pnlForm.add(cmbRoom, "height 35");

        // Buttons
        JPanel pnlBtns = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlBtns.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.PRIMARY_COLOR, "⟲");
        pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnDelete); pnlBtns.add(btnClear);
        pnlForm.add(pnlBtns, "gapy 10");

        pnlMgmt.add(pnlForm, BorderLayout.WEST);

        // Table bên phải
        String[] columns = {"ID", "Lớp học", "Ngày học", "Bắt đầu", "Kết thúc", "Phòng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSchedule = new JTable(tableModel);
        setupTable(tblSchedule);
        pnlMgmt.add(new JScrollPane(tblSchedule), BorderLayout.CENTER);

        return pnlMgmt;
    }

    @Override
    protected void loadTableData() {
        if (scheduleController == null) return;
        List<Schedule> list = scheduleController.getAllSchedules();
        
        // 1. Cập nhật Bảng (Staff view)
        tableModel.setRowCount(0);
        for (Schedule s : list) {
            tableModel.addRow(new Object[]{
                s.getSchedule_id(),
                s.getClazz() != null ? s.getClazz().getClassName() : "N/A",
                s.getStudyDate() != null ? s.getStudyDate().format(dateFormatter) : "",
                s.getStartTime() != null ? s.getStartTime().format(timeFormatter) : "",
                s.getEndTime() != null ? s.getEndTime().format(timeFormatter) : "",
                s.getRoom() != null ? s.getRoom().getRoomName() : "Chưa có"
            });
        }

        // 2. Cập nhật Lưới lịch (Teacher view)
        updateCalendarGrid(list);
    }

    private void updateCalendarGrid(List<Schedule> list) {
        pnlCalendarGrid.removeAll();
        
        // Nhóm lịch học theo Thứ trong tuần (1=Monday, 7=Sunday)
        Map<Integer, List<Schedule>> scheduleByDay = list.stream()
            .filter(s -> s.getStudyDate() != null)
            .collect(Collectors.groupingBy(s -> s.getStudyDate().getDayOfWeek().getValue()));

        for (int i = 1; i <= 7; i++) {
            JPanel pnlDayColumn = new JPanel(new MigLayout("wrap 1, inset 5, fillx", "[fill]", "[]5"));
            pnlDayColumn.setBackground(Color.WHITE);
            pnlDayColumn.setBorder(new LineBorder(new Color(230, 230, 230)));

            List<Schedule> daySchedules = scheduleByDay.get(i);
            if (daySchedules != null) {
                // Sắp xếp theo giờ bắt đầu
                daySchedules.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                for (Schedule s : daySchedules) {
                    pnlDayColumn.add(createScheduleCard(s));
                }
            }
            pnlCalendarGrid.add(pnlDayColumn);
        }
        pnlCalendarGrid.revalidate();
        pnlCalendarGrid.repaint();
    }

    private JPanel createScheduleCard(Schedule s) {
        JPanel card = new JPanel(new MigLayout("wrap 1, inset 8", "[fill]", "[]2[]2[]"));
        card.setBackground(new Color(255, 107, 53, 30)); // Light Orange
        card.setBorder(new LineBorder(UIHelper.PRIMARY_COLOR, 1, true));

        JLabel lblTime = new JLabel(s.getStartTime().format(timeFormatter) + " - " + s.getEndTime().format(timeFormatter));
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTime.setForeground(UIHelper.SECONDARY_COLOR);

        JLabel lblClass = new JLabel(s.getClazz() != null ? s.getClazz().getClassName() : "Unknown");
        lblClass.setFont(UIHelper.BOLD_FONT);

        JLabel lblRoom = new JLabel("📍 " + (s.getRoom() != null ? s.getRoom().getRoomName() : "No Room"));
        lblRoom.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRoom.setForeground(Color.GRAY);

        card.add(lblTime);
        card.add(lblClass);
        card.add(lblRoom);

        return card;
    }

    @Override
    protected void handleEvents() {
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> {
            // Logic thêm (giữ nguyên từ bản cũ nhưng tối ưu UI)
            loadTableData();
        });
        tblSchedule.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });
    }

    private void fillFormFromTable() {
        int row = tblSchedule.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            selectedSchedule = scheduleController.getScheduleById(id.intValue());
            if (selectedSchedule != null) {
                cmbClass.setSelectedItem(selectedSchedule.getClazz());
                txtStudyDate.setText(selectedSchedule.getStudyDate().format(dateFormatter));
                txtStartTime.setText(selectedSchedule.getStartTime().format(timeFormatter));
                txtEndTime.setText(selectedSchedule.getEndTime().format(timeFormatter));
                cmbRoom.setSelectedItem(selectedSchedule.getRoom());
            }
        }
    }

    private void clearForm() {
        txtStudyDate.setText(""); txtStartTime.setText(""); txtEndTime.setText("");
        tblSchedule.clearSelection();
        selectedSchedule = null;
    }

    private void loadComboBoxData() {
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) cmbClass.addItem(c);
        
        List<Room> rooms = roomDAO.findAll();
        cmbRoom.removeAllItems();
        cmbRoom.addItem(null);
        for (Room r : rooms) cmbRoom.addItem(r);
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new ScheduleManagerFrame().setVisible(true));
    }
}
