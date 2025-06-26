package com.mycompany.ui;

import com.mycompany.dao.TaskDAO;
import com.mycompany.model.Task;
import com.mycompany.service.I18nService;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class MainFrame extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField taskField, dueDateField;
    private JButton addButton, deleteButton, completeButton;
    private TaskDAO taskDAO;
    private JMenuBar menuBar;
    private JMenu fileMenu, languageMenu;
    private JMenuItem enMenuItem, idMenuItem;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MainFrame() {
        taskDAO = new TaskDAO();
        I18nService.setLocale(new Locale("id", "ID")); // Default Bahasa Indonesia

        initComponents();
        layoutComponents();
        addListeners();
        updateUI();
    }

    private void initComponents() {
        setTitle(I18nService.getString("app.title"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);

        // Form
        taskField = new JTextField(30);
        dueDateField = new JTextField(16);
        addButton = new JButton();
        deleteButton = new JButton();
        completeButton = new JButton();
        
        // Menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        languageMenu = new JMenu();
        enMenuItem = new JMenuItem();
        idMenuItem = new JMenuItem();
    }

    private void layoutComponents() {
        // Panel Input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel(I18nService.getString("label.task")));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel(I18nService.getString("label.duedate")));
        inputPanel.add(dueDateField);
        inputPanel.add(addButton);

        // Panel Tombol
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);

        // Main Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Menu Layout
        languageMenu.add(enMenuItem);
        languageMenu.add(idMenuItem);
        fileMenu.add(languageMenu);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private void updateUIText() {
        setTitle(I18nService.getString("app.title"));
        // Labels
        ((JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(0)).setText(I18nService.getString("label.task"));
        ((JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(2)).setText(I18nService.getString("label.duedate"));
        // Buttons
        addButton.setText(I18nService.getString("button.add"));
        deleteButton.setText(I18nService.getString("button.delete"));
        completeButton.setText(I18nService.getString("button.complete"));
        // Menu
        fileMenu.setText(I18nService.getString("menu.file"));
        languageMenu.setText(I18nService.getString("menu.language"));
        enMenuItem.setText(I18nService.getString("menu.lang.en"));
        idMenuItem.setText(I18nService.getString("menu.lang.id"));
        // Table Header
        tableModel.setColumnIdentifiers(new String[]{
            I18nService.getString("table.header.description"),
            I18nService.getString("table.header.duedate"),
            I18nService.getString("table.header.status")
        });
    }

    private void addListeners() {
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> toggleCompleteTask());

        idMenuItem.addActionListener(e -> switchLanguage(new Locale("id", "ID")));
        enMenuItem.addActionListener(e -> switchLanguage(new Locale("en", "US")));
    }
    
    private void switchLanguage(Locale locale) {
        I18nService.setLocale(locale);
        updateUI();
    }

    private void updateUI() {
        updateUIText();
        loadTasks();
    }

    private void loadTasks() {
        tableModel.setRowCount(0); // Clear table
        List<Task> tasks = taskDAO.findAll();
        for (Task task : tasks) {
            String status = task.isCompleted() ? I18nService.getString("status.completed") : I18nService.getString("status.pending");
            String dueDateStr = task.getDueDate() != null ? task.getDueDate().format(DATE_TIME_FORMATTER) : "N/A";
            tableModel.addRow(new Object[]{
                task.getDescription(),
                dueDateStr,
                status
            });
            // Simpan ObjectId di baris terakhir, tapi jangan tampilkan
            tableModel.setValueAt(task.getId(), tableModel.getRowCount() - 1, 0); 
            // Sembunyikan ObjectId dengan menimpa nilai sel tampilan
            taskTable.setValueAt(task.getDescription(), tableModel.getRowCount() - 1, 0); 
        }
    }
    
    private void addTask() {
        String description = taskField.getText();
        String dueDateStr = dueDateField.getText();
        if (description.isEmpty()) return;

        try {
            LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, DATE_TIME_FORMATTER);
            Task task = new Task();
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setCompleted(false);
            taskDAO.save(task);
            
            taskField.setText("");
            dueDateField.setText("");
            loadTasks();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, I18nService.getString("error.invalid.date"), 
                    I18nService.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Ambil ObjectId dari data model, bukan dari sel yang terlihat
            Object idObject = tableModel.getValueAt(selectedRow, 0);
            if (idObject instanceof org.bson.types.ObjectId) {
                taskDAO.deleteById((org.bson.types.ObjectId) idObject);
                loadTasks();
            }
        } else {
            JOptionPane.showMessageDialog(this, I18nService.getString("error.notask.selected"),
                    I18nService.getString("error.title"), JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void toggleCompleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            Object idObject = tableModel.getValueAt(selectedRow, 0);
            if (idObject instanceof org.bson.types.ObjectId) {
                List<Task> tasks = taskDAO.findAll();
                Task taskToUpdate = tasks.stream()
                        .filter(t -> t.getId().equals(idObject))
                        .findFirst().orElse(null);
                
                if (taskToUpdate != null) {
                    taskToUpdate.setCompleted(!taskToUpdate.isCompleted());
                    taskDAO.update(taskToUpdate);
                    loadTasks();
                }
            }
        }
    }
}
