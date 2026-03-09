package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.ScheduleController;
import com.trungtamdaotao.model.dao.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ScheduleManagerFrame extends JFrame {
    private final ScheduleController scheduleController;
    private final ClassController classController;
    private final IRoomDAO roomDAO;
    
    private JTable tblSchedule;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtStudyDate, txtStartTime, txtEndTime;
    private JTextField txtSearchDate, txtSearchStartDate, txtSearchEndDate;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<Room> cmbRoom;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearchByDate, btnSearchByClass, btnSearchByRoom;
    private JButton btnSearchByDateRange, btnReload, btnRefreshCombo;
    
    // Selected schedule for update/delete
    private Schedule selectedSchedule;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleManagerFrame() {
        this.scheduleController = new ScheduleController();
        this.classController = new ClassController();
        this.roomDAO = new RoomDAOImpl();
        
        initComponents();
        loadComboBoxData();
        loadTableData();
        
        setTitle("Quản lý Xếp lịch học - MIS English Center");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Toolbar phía trên
        JPanel pnlToolbar = createToolbarPanel();
        add(pnlToolbar, BorderLayout.NORTH);
        
        // Panel chính: Form bên trái + Table bên phải
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pnlForm = createFormPanel();
        pnlMain.add(pnlForm, BorderLayout.WEST);
        
        JPanel pnlTable = createTablePanel();
        pnlMain.add(pnlTable, BorderLayout.CENTER);
        
        add(pnlMain, BorderLayout.CENTER);
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Công cụ tìm kiếm và lọc"));
        panel.setBackground(new Color(240, 248, 255));
        
        // Search by date
        panel.add(new JLabel("Tìm theo ngày:"));
        txtSearchDate = new JTextField(12);
        txtSearchDate.setToolTipText("yyyy-MM-dd");
        panel.add(txtSearchDate);
        
        btnSearchByDate = new JButton("🔍 Tìm theo ngày");
        panel.add(btnSearchByDate);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Search by date range
        panel.add(new JLabel("Từ:"));
        txtSearchStartDate = new JTextField(10);
        txtSearchStartDate.setToolTipText("yyyy-MM-dd");
        panel.add(txtSearchStartDate);
        
        panel.add(new JLabel("Đến:"));
        txtSearchEndDate = new JTextField(10);
        txtSearchEndDate.setToolTipText("yyyy-MM-dd");
        panel.add(txtSearchEndDate);
        
        btnSearchByDateRange = new JButton("📅 Tìm theo khoảng");
        panel.add(btnSearchByDateRange);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Other filters
        btnSearchByClass = new JButton("📚 Lọc theo lớp");
        btnSearchByRoom = new JButton("🏫 Lọc theo phòng");
        btnReload = new JButton("⟳ Tải lại tất cả");
        
        panel.add(btnSearchByClass);
        panel.add(btnSearchByRoom);
        panel.add(btnReload);
        
        // Event handlers
        btnSearchByDate.addActionListener(e -> searchByDate());
        btnSearchByDateRange.addActionListener(e -> searchByDateRange());
        btnSearchByClass.addActionListener(e -> filterByClass());
        btnSearchByRoom.addActionListener(e -> filterByRoom());
        btnReload.addActionListener(e -> loadTableData());
        
        // Enter key for search
        txtSearchDate.addActionListener(e -> searchByDate());
        
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin lịch học"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(450, 700));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Class
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Lớp học: *"), gbc);
        gbc.gridx = 1;
        JPanel pnlClass = new JPanel(new BorderLayout(5, 0));
        cmbClass = new JComboBox<>();
        pnlClass.add(cmbClass, BorderLayout.CENTER);
        btnRefreshCombo = new JButton("⟳");
        btnRefreshCombo.setPreferredSize(new Dimension(40, 25));
        btnRefreshCombo.setToolTipText("Làm mới danh sách");
        btnRefreshCombo.addActionListener(e -> loadComboBoxData());
        pnlClass.add(btnRefreshCombo, BorderLayout.EAST);
        panel.add(pnlClass, gbc);
        row++;
        
        // Study Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày học: *"), gbc);
        gbc.gridx = 1;
        txtStudyDate = new JTextField(20);
        txtStudyDate.setToolTipText("Định dạng: yyyy-MM-dd (VD: 2026-03-10)");
        panel.add(txtStudyDate, gbc);
        row++;
        
        // Start Time
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Giờ bắt đầu: *"), gbc);
        gbc.gridx = 1;
        txtStartTime = new JTextField(20);
        txtStartTime.setToolTipText("Định dạng: HH:mm (VD: 08:00)");
        panel.add(txtStartTime, gbc);
        row++;
        
        // End Time
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Giờ kết thúc: *"), gbc);
        gbc.gridx = 1;
        txtEndTime = new JTextField(20);
        txtEndTime.setToolTipText("Định dạng: HH:mm (VD: 10:00)");
        panel.add(txtEndTime, gbc);
        row++;
        
        // Room
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phòng học:"), gbc);
        gbc.gridx = 1;
        cmbRoom = new JComboBox<>();
        panel.add(cmbRoom, gbc);
        row++;
        
        // Note panel
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel pnlNote = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNote.add(new JLabel("<html><i>* Trường bắt buộc<br>" +
                "Định dạng ngày: yyyy-MM-dd<br>" +
                "Định dạng giờ: HH:mm</i></html>"));
        panel.add(pnlNote, gbc);
        row++;
        
        // Spacer
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weighty = 0.5;
        panel.add(Box.createVerticalStrut(10), gbc);
        row++;
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        btnAdd = new JButton("➕ Thêm mới");
        btnUpdate = new JButton("✏️ Cập nhật");
        btnDelete = new JButton("🗑️ Xóa");
        btnClear = new JButton("🔄 Làm mới form");
        
        // Set colors
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);
        
        panel.add(pnlButtons, gbc);
        
        // Event handlers
        btnAdd.addActionListener(e -> addSchedule());
        btnUpdate.addActionListener(e -> updateSchedule());
        btnDelete.addActionListener(e -> deleteSchedule());
        btnClear.addActionListener(e -> clearForm());
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách lịch học"));
        
        // Table
        String[] columns = {"ID", "Lớp học", "Khóa học", "Ngày học", 
                           "Giờ bắt đầu", "Giờ kết thúc", "Phòng học"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSchedule = new JTable(tableModel);
        tblSchedule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSchedule.setRowHeight(25);
        tblSchedule.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        tblSchedule.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblSchedule.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblSchedule.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblSchedule.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblSchedule.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblSchedule.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblSchedule.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(tblSchedule);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Table selection listener
        tblSchedule.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectScheduleFromTable();
            }
        });
        
        // Status bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.add(new JLabel("💡 Nhấp đúp vào dòng để chọn và chỉnh sửa"));
        panel.add(pnlStatus, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadComboBoxData() {
        // Load classes
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) {
            cmbClass.addItem(c);
        }
        
        // Custom renderer for classes
        cmbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassEntity) {
                    ClassEntity c = (ClassEntity) value;
                    setText(c.getClassName() + " (" + 
                            (c.getCourse() != null ? c.getCourse().getCourseName() : "N/A") + ")");
                }
                return this;
            }
        });
        
        // Load rooms (thêm null option)
        List<Room> rooms = roomDAO.findAll();
        cmbRoom.removeAllItems();
        cmbRoom.addItem(null); // Option để không chọn room
        for (Room r : rooms) {
            cmbRoom.addItem(r);
        }
        
        // Custom renderer for rooms
        cmbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chưa phân phòng --");
                } else if (value instanceof Room) {
                    setText(((Room) value).getRoomName());
                }
                return this;
            }
        });
    }

    private void addSchedule() {
        try {
            Schedule schedule = new Schedule();
            schedule.setClazz((ClassEntity) cmbClass.getSelectedItem());
            
            // Parse study date
            String studyDateStr = txtStudyDate.getText().trim();
            if (!studyDateStr.isEmpty()) {
                try {
                    schedule.setStudyDate(LocalDate.parse(studyDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Ngày học không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Parse start time
            String startTimeStr = txtStartTime.getText().trim();
            if (!startTimeStr.isEmpty()) {
                try {
                    schedule.setStartTime(LocalTime.parse(startTimeStr, timeFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Giờ bắt đầu không đúng định dạng (HH:mm)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Parse end time
            String endTimeStr = txtEndTime.getText().trim();
            if (!endTimeStr.isEmpty()) {
                try {
                    schedule.setEndTime(LocalTime.parse(endTimeStr, timeFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Giờ kết thúc không đúng định dạng (HH:mm)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            schedule.setRoom((Room) cmbRoom.getSelectedItem());
            
            String message = scheduleController.createSchedule(schedule);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule() {
        if (selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn lịch học từ bảng để cập nhật!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            selectedSchedule.setClazz((ClassEntity) cmbClass.getSelectedItem());
            
            // Parse study date
            String studyDateStr = txtStudyDate.getText().trim();
            if (!studyDateStr.isEmpty()) {
                try {
                    selectedSchedule.setStudyDate(LocalDate.parse(studyDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Ngày học không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Parse start time
            String startTimeStr = txtStartTime.getText().trim();
            if (!startTimeStr.isEmpty()) {
                try {
                    selectedSchedule.setStartTime(LocalTime.parse(startTimeStr, timeFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Giờ bắt đầu không đúng định dạng (HH:mm)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Parse end time
            String endTimeStr = txtEndTime.getText().trim();
            if (!endTimeStr.isEmpty()) {
                try {
                    selectedSchedule.setEndTime(LocalTime.parse(endTimeStr, timeFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Giờ kết thúc không đúng định dạng (HH:mm)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            selectedSchedule.setRoom((Room) cmbRoom.getSelectedItem());
            
            String message = scheduleController.updateSchedule(selectedSchedule);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSchedule() {
        if (selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn lịch học từ bảng để xóa!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa lịch học này?\n" +
                "Lớp: " + (selectedSchedule.getClazz() != null ? 
                        selectedSchedule.getClazz().getClassName() : "N/A") + "\n" +
                "Ngày: " + selectedSchedule.getStudyDate() + "\n" +
                "Giờ: " + selectedSchedule.getStartTime() + " - " + selectedSchedule.getEndTime(),
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String message = scheduleController.deleteSchedule(
                    selectedSchedule.getSchedule_id().intValue());
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtStudyDate.setText("");
        txtStartTime.setText("");
        txtEndTime.setText("");
        if (cmbClass.getItemCount() > 0) cmbClass.setSelectedIndex(0);
        if (cmbRoom.getItemCount() > 0) cmbRoom.setSelectedIndex(0);
        selectedSchedule = null;
        tblSchedule.clearSelection();
    }

    private void selectScheduleFromTable() {
        int selectedRow = tblSchedule.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            selectedSchedule = scheduleController.getScheduleById(id.intValue());
            
            if (selectedSchedule != null) {
                // Tìm và chọn Class theo ID
                if (selectedSchedule.getClazz() != null) {
                    Long classId = selectedSchedule.getClazz().getClass_id();
                    for (int i = 0; i < cmbClass.getItemCount(); i++) {
                        ClassEntity c = cmbClass.getItemAt(i);
                        if (c != null && c.getClass_id().equals(classId)) {
                            cmbClass.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (cmbClass.getItemCount() > 0) {
                    cmbClass.setSelectedIndex(0);
                }
                
                txtStudyDate.setText(selectedSchedule.getStudyDate() != null ? 
                        selectedSchedule.getStudyDate().format(dateFormatter) : "");
                txtStartTime.setText(selectedSchedule.getStartTime() != null ? 
                        selectedSchedule.getStartTime().format(timeFormatter) : "");
                txtEndTime.setText(selectedSchedule.getEndTime() != null ? 
                        selectedSchedule.getEndTime().format(timeFormatter) : "");
                
                // Tìm và chọn Room theo ID
                if (selectedSchedule.getRoom() != null) {
                    Long roomId = selectedSchedule.getRoom().getRoom_id();
                    for (int i = 0; i < cmbRoom.getItemCount(); i++) {
                        Room r = cmbRoom.getItemAt(i);
                        if (r != null && r.getRoom_id().equals(roomId)) {
                            cmbRoom.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbRoom.setSelectedIndex(0); // Chọn "-- Chưa phân phòng --"
                }
            }
        }
    }

    private void searchByDate() {
        String dateStr = txtSearchDate.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày cần tìm!", 
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            List<Schedule> schedules = scheduleController.getSchedulesByDate(date);
            renderTable(schedules);
            JOptionPane.showMessageDialog(this, 
                    "Tìm thấy " + schedules.size() + " lịch học vào ngày " + date,
                    "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi: Ngày không đúng định dạng (yyyy-MM-dd)!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByDateRange() {
        String startDateStr = txtSearchStartDate.getText().trim();
        String endDateStr = txtSearchEndDate.getText().trim();
        
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập đầy đủ ngày bắt đầu và kết thúc!", 
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
            
            if (startDate.isAfter(endDate)) {
                JOptionPane.showMessageDialog(this, 
                        "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Schedule> schedules = scheduleController.getSchedulesByDateRange(startDate, endDate);
            renderTable(schedules);
            JOptionPane.showMessageDialog(this, 
                    "Tìm thấy " + schedules.size() + " lịch học từ " + startDate + " đến " + endDate,
                    "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi: Ngày không đúng định dạng (yyyy-MM-dd)!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByClass() {
        ClassEntity selectedClass = (ClassEntity) cmbClass.getSelectedItem();
        if (selectedClass != null) {
            List<Schedule> schedules = scheduleController.getSchedulesByClass(selectedClass);
            renderTable(schedules);
            JOptionPane.showMessageDialog(this, 
                    "Đã lọc " + schedules.size() + " lịch học của lớp: " + 
                    selectedClass.getClassName(),
                    "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterByRoom() {
        Room selectedRoom = (Room) cmbRoom.getSelectedItem();
        if (selectedRoom != null) {
            List<Schedule> schedules = scheduleController.getSchedulesByRoom(selectedRoom);
            renderTable(schedules);
            JOptionPane.showMessageDialog(this, 
                    "Đã lọc " + schedules.size() + " lịch học tại phòng: " + 
                    selectedRoom.getRoomName(),
                    "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng học!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadTableData() {
        List<Schedule> schedules = scheduleController.getAllSchedules();
        renderTable(schedules);
    }

    private void renderTable(List<Schedule> schedules) {
        tableModel.setRowCount(0);
        if (schedules != null && !schedules.isEmpty()) {
            for (Schedule s : schedules) {
                tableModel.addRow(new Object[]{
                        s.getSchedule_id(),
                        s.getClazz() != null ? s.getClazz().getClassName() : "N/A",
                        s.getClazz() != null && s.getClazz().getCourse() != null ? 
                                s.getClazz().getCourse().getCourseName() : "N/A",
                        s.getStudyDate() != null ? s.getStudyDate().format(dateFormatter) : "",
                        s.getStartTime() != null ? s.getStartTime().format(timeFormatter) : "",
                        s.getEndTime() != null ? s.getEndTime().format(timeFormatter) : "",
                        s.getRoom() != null ? s.getRoom().getRoomName() : "Chưa có"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScheduleManagerFrame().setVisible(true));
    }
}
