package com.trungtamdaotao.view.academic;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.ScheduleController;
import com.trungtamdaotao.model.dao.operations.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.UserSession;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleManagerFrame extends BaseManagerFrame {
    private final ScheduleController scheduleController;
    private final ClassController classController;
    private final IRoomDAO roomDAO;
    
    private JTable tblSchedule;
    private DefaultTableModel tableModel;
    
    // Form fields
    private DatePicker dpStudyDate;
    private TimePicker tpStartTime, tpEndTime;
    private JTextField txtSearchDate;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<Room> cmbRoom;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearchByDate, btnSearchByClass, btnSearchByRoom;
    private JButton btnReload;
    
    // Selected schedule for update/delete
    private Schedule selectedSchedule;

    public ScheduleManagerFrame() {
        super("Quản lý Xếp lịch học", 
              new AccountRole[]{AccountRole.ADMIN, AccountRole.STAFF}, 
              new StaffRole[]{StaffRole.MANAGER, StaffRole.CONSULTANT});
        this.scheduleController = new ScheduleController();
        this.classController = new ClassController();
        this.roomDAO = new RoomDAOImpl();
        
        loadComboBoxData();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblSearch = new JLabel("Tìm ngày (yyyy-MM-dd):");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);
        
        txtSearchDate = new JTextField(10);
        pnlToolbar.add(txtSearchDate);
        
        btnSearchByDate = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearchByDate.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearchByDate);
        
        pnlToolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        btnSearchByClass = UIHelper.createStandardButton("Lọc Lớp", Color.WHITE, "📚");
        btnSearchByClass.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearchByClass);
        
        btnSearchByRoom = UIHelper.createStandardButton("Lọc Phòng", Color.WHITE, "🏫");
        btnSearchByRoom.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearchByRoom);
        
        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin lịch học");
        pnlForm.setPreferredSize(new Dimension(420, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Lớp học: *"), gbc);
        gbc.gridx = 1;
        cmbClass = new JComboBox<>();
        pnlForm.add(cmbClass, gbc);
        row++;

        // DatePicker cho Ngày học
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Ngày học: *"), gbc);
        gbc.gridx = 1;
        dpStudyDate = UIHelper.createDatePicker();
        pnlForm.add(dpStudyDate, gbc);
        row++;

        // TimePickers
        TimePickerSettings timeSettings1 = new TimePickerSettings();
        timeSettings1.use24HourClockFormat();
        tpStartTime = new TimePicker(timeSettings1);
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Giờ bắt đầu: *"), gbc);
        gbc.gridx = 1;
        pnlForm.add(tpStartTime, gbc);
        row++;

        TimePickerSettings timeSettings2 = new TimePickerSettings();
        timeSettings2.use24HourClockFormat();
        tpEndTime = new TimePicker(timeSettings2);
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Giờ kết thúc: *"), gbc);
        gbc.gridx = 1;
        pnlForm.add(tpEndTime, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Phòng học:"), gbc);
        gbc.gridx = 1;
        cmbRoom = new JComboBox<>();
        pnlForm.add(cmbRoom, gbc);
        row++;

        // Buttons Panel
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.PRIMARY_COLOR, "⟲");
        
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        if (UserSession.getPermissions().canDelete()) {
            pnlButtons.add(btnDelete);
        }
        pnlButtons.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.WEST);

        // --- Table (CENTER) ---
        String[] columns = {"ID", "Lớp học", "Khóa học", "Ngày học", "Bắt đầu", "Kết thúc", "Phòng học"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSchedule = new JTable(tableModel);
        setupTable(tblSchedule);
        add(new JScrollPane(tblSchedule), BorderLayout.CENTER);
    }

    @Override
    protected void handleEvents() {
        btnSearchByDate.addActionListener(e -> searchByDate());
        btnSearchByClass.addActionListener(e -> filterByClass());
        btnSearchByRoom.addActionListener(e -> filterByRoom());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> addSchedule());
        btnUpdate.addActionListener(e -> updateSchedule());
        if (btnDelete.getParent() != null) {
            btnDelete.addActionListener(e -> deleteSchedule());
        }
        btnClear.addActionListener(e -> clearForm());
        
        tblSchedule.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectScheduleFromTable();
        });
    }

    private void loadComboBoxData() {
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) cmbClass.addItem(c);
        
        cmbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassEntity) {
                    ClassEntity c = (ClassEntity) value;
                    setText(c.getClassName() + " (" + (c.getCourse() != null ? c.getCourse().getCourseName() : "N/A") + ")");
                }
                return this;
            }
        });
        
        List<Room> rooms = roomDAO.findAll();
        cmbRoom.removeAllItems();
        cmbRoom.addItem(null);
        for (Room r : rooms) cmbRoom.addItem(r);
        
        cmbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chưa phân phòng --" : ((Room) value).getRoomName());
                return this;
            }
        });
    }

    private void addSchedule() {
        try {
            Schedule s = new Schedule();
            s.setClazz((ClassEntity) cmbClass.getSelectedItem());
            s.setStudyDate(dpStudyDate.getDate());
            s.setStartTime(tpStartTime.getTime());
            s.setEndTime(tpEndTime.getTime());
            s.setRoom((Room) cmbRoom.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, scheduleController.createSchedule(s));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateSchedule() {
        if (selectedSchedule == null) return;
        try {
            selectedSchedule.setClazz((ClassEntity) cmbClass.getSelectedItem());
            selectedSchedule.setStudyDate(dpStudyDate.getDate());
            selectedSchedule.setStartTime(tpStartTime.getTime());
            selectedSchedule.setEndTime(tpEndTime.getTime());
            selectedSchedule.setRoom((Room) cmbRoom.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, scheduleController.updateSchedule(selectedSchedule));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteSchedule() {
        if (selectedSchedule == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa lịch học này?");
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, scheduleController.deleteSchedule(selectedSchedule.getSchedule_id().intValue()));
            loadTableData();
            clearForm();
        }
    }

    private void clearForm() {
        dpStudyDate.clear(); tpStartTime.clear(); tpEndTime.clear();
        if (cmbClass.getItemCount() > 0) cmbClass.setSelectedIndex(0);
        cmbRoom.setSelectedIndex(0); selectedSchedule = null;
        tblSchedule.clearSelection();
    }

    private void selectScheduleFromTable() {
        int row = tblSchedule.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            selectedSchedule = scheduleController.getScheduleById(id.intValue());
            if (selectedSchedule != null) {
                cmbClass.setSelectedItem(selectedSchedule.getClazz());
                dpStudyDate.setDate(selectedSchedule.getStudyDate());
                tpStartTime.setTime(selectedSchedule.getStartTime());
                tpEndTime.setTime(selectedSchedule.getEndTime());
                cmbRoom.setSelectedItem(selectedSchedule.getRoom());
            }
        }
    }

    private void searchByDate() {
        try {
            LocalDate date = LocalDate.parse(txtSearchDate.getText().trim());
            renderTable(scheduleController.getSchedulesByDate(date));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ (yyyy-MM-dd)");
        }
    }

    private void filterByClass() {
        ClassEntity c = (ClassEntity) cmbClass.getSelectedItem();
        if (c != null) renderTable(scheduleController.getSchedulesByClass(c));
    }

    private void filterByRoom() {
        Room r = (Room) cmbRoom.getSelectedItem();
        if (r != null) renderTable(scheduleController.getSchedulesByRoom(r));
    }

    @Override
    protected void loadTableData() {
        if (scheduleController != null) renderTable(scheduleController.getAllSchedules());
    }

    private void renderTable(List<Schedule> list) {
        tableModel.setRowCount(0);
        for (Schedule s : list) {
            tableModel.addRow(new Object[]{
                s.getSchedule_id(),
                s.getClazz() != null ? s.getClazz().getClassName() : "N/A",
                s.getClazz() != null && s.getClazz().getCourse() != null ? s.getClazz().getCourse().getCourseName() : "N/A",
                s.getStudyDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getRoom() != null ? s.getRoom().getRoomName() : "Chưa có"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScheduleManagerFrame().setVisible(true));
    }
}
