package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.ScheduleController;
import com.trungtamdaotao.model.dao.impl.RoomDAOImpl;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel Lich hoc tich hop (Visual + Data).
 */
public class ScheduleManagerPanel extends BaseManagerPanel {
    private final ScheduleController scheduleController;
    private final ClassController classController;
    private final IRoomDAO roomDAO;
    
    private JTabbedPane tabbedPane;
    private JPanel pnlCalendarGrid;
    private JTable tblSchedule;
    private DefaultTableModel tableModel;
    private JTextField txtStudyDate, txtStartTime, txtEndTime;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<Room> cmbRoom;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleManagerPanel() {
        super();
        this.scheduleController = new ScheduleController();
        this.classController = new ClassController();
        this.roomDAO = new RoomDAOImpl();
        loadComboBoxData();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        // Tab 1: Visual Calendar
        JPanel pnlCalendar = new JPanel(new BorderLayout());
        JPanel pnlHeader = new JPanel(new GridLayout(1, 7, 5, 0));
        pnlHeader.setBackground(UIHelper.ACCENT_COLOR);
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(UIHelper.BOLD_FONT); lbl.setForeground(Color.WHITE);
            lbl.setBorder(new EmptyBorder(10,0,10,0)); pnlHeader.add(lbl);
        }
        pnlCalendar.add(pnlHeader, BorderLayout.NORTH);
        pnlCalendarGrid = new JPanel(new GridLayout(1, 7, 5, 5));
        pnlCalendarGrid.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlCalendar.add(new JScrollPane(pnlCalendarGrid), BorderLayout.CENTER);
        tabbedPane.addTab("LICH TUAN", pnlCalendar);

        // Tab 2: Management
        JPanel pnlMgmt = new JPanel(new BorderLayout());
        JPanel pnlForm = new JPanel(new MigLayout("wrap 1, inset 20, fillx", "[fill]", "[]20[]10[]10[]10[]10[]10[]20[]"));
        pnlForm.setBackground(Color.WHITE); pnlForm.setPreferredSize(new Dimension(320, 0));
        pnlForm.add(new JLabel("CHI TIET LICH HOC"), "center");
        pnlForm.add(createFieldLabel("Lop học:")); cmbClass = new JComboBox<>(); pnlForm.add(cmbClass, "height 35");
        pnlForm.add(createFieldLabel("Ngay (yyyy-mm-dd):")); pnlForm.add(txtStudyDate = new JTextField(), "height 35");
        pnlForm.add(createFieldLabel("Bat dau:")); pnlForm.add(txtStartTime = new JTextField(), "height 35");
        pnlForm.add(createFieldLabel("Ket thuc:")); pnlForm.add(txtEndTime = new JTextField(), "height 35");
        pnlForm.add(createFieldLabel("Phong:")); cmbRoom = new JComboBox<>(); pnlForm.add(cmbRoom, "height 35");
        JPanel pnlBtns = new JPanel(new GridLayout(2, 2, 5, 5)); pnlBtns.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Them", UIHelper.SUCCESS_COLOR, "");
        btnUpdate = UIHelper.createStandardButton("Sua", UIHelper.WARNING_COLOR, "");
        btnDelete = UIHelper.createStandardButton("Xoa", UIHelper.DANGER_COLOR, "");
        btnClear = UIHelper.createStandardButton("Moi", UIHelper.ACCENT_COLOR, "");
        pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnDelete); pnlBtns.add(btnClear);
        pnlForm.add(pnlBtns, "gapy 10");
        pnlMgmt.add(pnlForm, BorderLayout.WEST);

        String[] cols = {"ID", "Lop", "Ngay", "Bat dau", "Ket thuc", "Phong"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblSchedule = new JTable(tableModel); setupTable(tblSchedule);
        pnlMgmt.add(new JScrollPane(tblSchedule), BorderLayout.CENTER);
        tabbedPane.addTab("QUAN LY", pnlMgmt);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadComboBoxData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
        roomDAO.findAll().forEach(cmbRoom::addItem);
    }

    @Override
    protected void loadTableData() {
        if (scheduleController == null) return;
        List<Schedule> list = scheduleController.getAllSchedules();
        tableModel.setRowCount(0);
        list.forEach(s -> tableModel.addRow(new Object[]{ s.getSchedule_id(), s.getClazz().getClassName(), s.getStudyDate(), s.getStartTime(), s.getEndTime(), s.getRoom() != null ? s.getRoom().getRoomName() : "No" }));
        updateCalendarGrid(list);
    }

    private void updateCalendarGrid(List<Schedule> list) {
        pnlCalendarGrid.removeAll();
        Map<Integer, List<Schedule>> byDay = list.stream().filter(s -> s.getStudyDate() != null).collect(Collectors.groupingBy(s -> s.getStudyDate().getDayOfWeek().getValue()));
        for (int i = 1; i <= 7; i++) {
            JPanel col = new JPanel(new MigLayout("wrap 1, inset 5, fillx", "[fill]")); col.setBackground(Color.WHITE);
            List<Schedule> dayList = byDay.get(i);
            if (dayList != null) {
                dayList.sort((a,b) -> a.getStartTime().compareTo(b.getStartTime()));
                dayList.forEach(s -> col.add(createCard(s)));
            }
            pnlCalendarGrid.add(col);
        }
        pnlCalendarGrid.revalidate(); pnlCalendarGrid.repaint();
    }

    private JPanel createCard(Schedule s) {
        JPanel c = new JPanel(new MigLayout("wrap 1, inset 5")); c.setBackground(new Color(255,107,53,20)); c.setBorder(new LineBorder(UIHelper.PRIMARY_COLOR));
        c.add(new JLabel(s.getStartTime() + "-" + s.getEndTime()));
        c.add(new JLabel(s.getClazz().getClassName()), "growx");
        return c;
    }

    @Override protected void handleEvents() { btnClear.addActionListener(e -> { txtStudyDate.setText(""); tblSchedule.clearSelection(); }); }
}
