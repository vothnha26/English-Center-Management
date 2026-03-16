package com.trungtamdaotao.view.academic;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.ScheduleController;
import com.trungtamdaotao.model.dao.operations.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel Lịch học nâng cao (Xem theo tuần + Điều hướng + DatePicker).
 */
public class ScheduleManagerPanel extends BaseManagerPanel {
    private final ScheduleController scheduleController;
    private final ClassController classController;
    private final IRoomDAO roomDAO;

    private JTabbedPane tabbedPane;
    private JPanel pnlCalendarGrid;
    private JTable tblSchedule;
    private DefaultTableModel tableModel;

    // Form fields (Streamlined Session Picker)
    private DatePicker dpStudyDate;
    private TimePicker tpStartTime, tpEndTime;
    private DatePicker dpFilter;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<Room> cmbRoom;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // Navigation
    private JButton btnPrevWeek, btnNextWeek, btnCurrentWeek;
    private JLabel lblWeekRange;
    private LocalDate currentMonday;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ScheduleManagerPanel() {
        super();
        this.scheduleController = new ScheduleController();
        this.classController = new ClassController();
        this.roomDAO = new RoomDAOImpl();
        this.currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        loadComboBoxData();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        // --- Tab 1: LỊCH TUẦN ---
        JPanel pnlCalendar = new JPanel(new BorderLayout());
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNav.setBackground(Color.WHITE);

        btnPrevWeek = UIHelper.createStandardButton("Tuần trước", UIHelper.ACCENT_COLOR, "◀");
        btnCurrentWeek = UIHelper.createStandardButton("Tuần này", UIHelper.PRIMARY_COLOR, "📅");
        btnNextWeek = UIHelper.createStandardButton("Tuần sau", UIHelper.ACCENT_COLOR, "▶");
        lblWeekRange = new JLabel("Khoảng thời gian tuần");
        lblWeekRange.setFont(UIHelper.BOLD_FONT);

        dpFilter = UIHelper.createDatePicker();
        pnlNav.add(btnPrevWeek);
        pnlNav.add(btnCurrentWeek);
        pnlNav.add(btnNextWeek);
        pnlNav.add(new JSeparator(SwingConstants.VERTICAL));
        pnlNav.add(new JLabel("Xem tuần từ ngày:"));
        pnlNav.add(dpFilter);
        pnlNav.add(Box.createHorizontalStrut(20));
        pnlNav.add(lblWeekRange);
        pnlCalendar.add(pnlNav, BorderLayout.NORTH);

        JPanel pnlGridWrapper = new JPanel(new BorderLayout());
        JPanel pnlDaysHeader = new JPanel(new GridLayout(1, 7, 5, 0));
        pnlDaysHeader.setBackground(UIHelper.PRIMARY_COLOR);
        String[] days = { "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật" };
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(UIHelper.BOLD_FONT);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
            pnlDaysHeader.add(lbl);
        }
        pnlGridWrapper.add(pnlDaysHeader, BorderLayout.NORTH);
        pnlCalendarGrid = new JPanel(new GridLayout(1, 7, 5, 5));
        pnlCalendarGrid.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlGridWrapper.add(pnlCalendarGrid, BorderLayout.CENTER);
        pnlCalendar.add(new JScrollPane(pnlGridWrapper), BorderLayout.CENTER);
        tabbedPane.addTab("LỊCH TUẦN", pnlCalendar);

        // --- Tab 2: QUẢN LÝ ---
        JPanel pnlMgmt = new JPanel(new BorderLayout());
        JPanel pnlForm = new JPanel(new MigLayout("wrap 1, inset 20, fillx", "[fill]", "[]20[]10[]10[]10[]20[]"));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setPreferredSize(new Dimension(400, 0));

        pnlForm.add(new JLabel("THÔNG TIN BUỔI HỌC"), "center, gapy 10");
        pnlForm.add(createFieldLabel("Lớp học:"));
        cmbClass = new JComboBox<>();
        pnlForm.add(cmbClass, "height 35");

        // Compact Time Selection Row
        pnlForm.add(createFieldLabel("Thời gian học (Ngày | Bắt đầu | Kết thúc):"));
        JPanel pnlTimePicker = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow][grow]"));
        pnlTimePicker.setOpaque(false);
        dpStudyDate = UIHelper.createDatePicker();
        tpStartTime = UIHelper.createTimePicker();
        tpEndTime = UIHelper.createTimePicker();
        pnlTimePicker.add(dpStudyDate, "growx");
        pnlTimePicker.add(tpStartTime, "growx");
        pnlTimePicker.add(tpEndTime, "growx");
        pnlForm.add(pnlTimePicker, "height 35");

        pnlForm.add(createFieldLabel("Phòng học:"));
        cmbRoom = new JComboBox<>();
        pnlForm.add(cmbRoom, "height 35");

        JPanel pnlBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlBtns.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.ACCENT_COLOR, "⟲");
        pnlBtns.add(btnAdd);
        pnlBtns.add(btnUpdate);
        pnlBtns.add(btnDelete);
        pnlBtns.add(btnClear);
        pnlForm.add(pnlBtns, "gapy 20");
        pnlMgmt.add(pnlForm, BorderLayout.WEST);

        String[] cols = { "ID", "Lớp học", "Ngày học", "Bắt đầu", "Kết thúc", "Phòng" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblSchedule = new JTable(tableModel);
        setupTable(tblSchedule);
        pnlMgmt.add(new JScrollPane(tblSchedule), BorderLayout.CENTER);
        tabbedPane.addTab("DANH SÁCH QUẢN LÝ", pnlMgmt);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadComboBoxData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
        roomDAO.findAll().forEach(cmbRoom::addItem);
    }

    @Override
    protected void loadTableData() {
        if (scheduleController == null)
            return;
        LocalDate sunday = currentMonday.plusDays(6);
        lblWeekRange.setText(
                String.format("Tuần: %s - %s", currentMonday.format(dateFormatter), sunday.format(dateFormatter)));

        List<Schedule> all = scheduleController.getAllSchedules();
        List<Schedule> week = all.stream()
                .filter(s -> !s.getStudyDate().isBefore(currentMonday) && !s.getStudyDate().isAfter(sunday))
                .sorted((a, b) -> a.getStudyDate().compareTo(b.getStudyDate()))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        week.forEach(s -> tableModel.addRow(new Object[] {
                s.getSchedule_id(), s.getClazz().getClassName(), s.getStudyDate(), s.getStartTime(), s.getEndTime(),
                s.getRoom() != null ? s.getRoom().getRoomName() : "N/A"
        }));
        updateCalendarGrid(week);
    }

    private void updateCalendarGrid(List<Schedule> list) {
        pnlCalendarGrid.removeAll();
        Map<Integer, List<Schedule>> byDay = list.stream()
                .collect(Collectors.groupingBy(s -> s.getStudyDate().getDayOfWeek().getValue()));
        for (int i = 1; i <= 7; i++) {
            JPanel col = new JPanel(new MigLayout("wrap 1, inset 5, fillx", "[fill]"));
            col.setBackground(Color.WHITE);
            col.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
            LocalDate dayDate = currentMonday.plusDays(i - 1);
            JLabel lblDate = new JLabel(dayDate.format(DateTimeFormatter.ofPattern("dd/MM")));
            lblDate.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblDate.setForeground(Color.GRAY);
            col.add(lblDate, "center, gapy 5");
            List<Schedule> dayList = byDay.get(i);
            if (dayList != null) {
                dayList.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                dayList.forEach(s -> col.add(createCard(s)));
            }
            pnlCalendarGrid.add(col);
        }
        pnlCalendarGrid.revalidate();
        pnlCalendarGrid.repaint();
    }

    private JPanel createCard(Schedule s) {
        JPanel c = new JPanel(new MigLayout("wrap 1, inset 8"));
        c.setBackground(new Color(52, 152, 219, 15));
        c.setBorder(new LineBorder(UIHelper.ACCENT_COLOR, 1, true));
        JLabel lblTime = new JLabel(s.getStartTime() + " - " + s.getEndTime());
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTime.setForeground(UIHelper.PRIMARY_COLOR);
        c.add(lblTime);
        JLabel lblName = new JLabel(
                "<html><body style='width: 80px'>" + s.getClazz().getClassName() + "</body></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        c.add(lblName, "growx");
        String roomName = s.getRoom() != null ? s.getRoom().getRoomName() : "Chưa phân phòng";
        JLabel lblRoom = new JLabel("Phòng: " + roomName);
        lblRoom.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRoom.setForeground(new Color(90, 90, 90));
        c.add(lblRoom, "growx");
        return c;
    }

    @Override
    protected void handleEvents() {
        btnPrevWeek.addActionListener(e -> {
            currentMonday = currentMonday.minusWeeks(1);
            loadTableData();
        });
        btnNextWeek.addActionListener(e -> {
            currentMonday = currentMonday.plusWeeks(1);
            loadTableData();
        });
        btnCurrentWeek.addActionListener(e -> {
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            loadTableData();
        });
        dpFilter.addDateChangeListener(e -> {
            if (e.getNewDate() != null) {
                currentMonday = e.getNewDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                loadTableData();
            }
        });

        btnClear.addActionListener(e -> {
            dpStudyDate.clear();
            tpStartTime.clear();
            tpEndTime.clear();
            tblSchedule.clearSelection();
        });
        btnAdd.addActionListener(e -> {
            Schedule s = getScheduleFromFields();
            if (s != null) {
                JOptionPane.showMessageDialog(this, scheduleController.createSchedule(s));
                loadTableData();
            }
        });
        btnUpdate.addActionListener(e -> {
            int row = tblSchedule.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn lịch học cần sửa!");
                return;
            }
            Schedule s = getScheduleFromFields();
            if (s != null) {
                s.setSchedule_id((Long) tableModel.getValueAt(row, 0));
                JOptionPane.showMessageDialog(this, scheduleController.updateSchedule(s));
                loadTableData();
            }
        });
        btnDelete.addActionListener(e -> {
            int row = tblSchedule.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn lịch học cần xóa!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                scheduleController.deleteSchedule(((Long) tableModel.getValueAt(row, 0)).intValue());
                loadTableData();
            }
        });
        tblSchedule.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                fillFieldsFromSelectedRow();
        });
    }

    private Schedule getScheduleFromFields() {
        try {
            Schedule s = new Schedule();
            s.setClazz((ClassEntity) cmbClass.getSelectedItem());
            s.setStudyDate(dpStudyDate.getDate());
            s.setStartTime(tpStartTime.getTime());
            s.setEndTime(tpEndTime.getTime());
            s.setRoom((Room) cmbRoom.getSelectedItem());
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    private void fillFieldsFromSelectedRow() {
        int row = tblSchedule.getSelectedRow();
        if (row >= 0) {
            Schedule s = scheduleController.getScheduleById(((Long) tableModel.getValueAt(row, 0)).intValue());
            if (s != null) {
                cmbClass.setSelectedItem(s.getClazz());
                dpStudyDate.setDate(s.getStudyDate());
                tpStartTime.setTime(s.getStartTime());
                tpEndTime.setTime(s.getEndTime());
                cmbRoom.setSelectedItem(s.getRoom());
            }
        }
    }
}
