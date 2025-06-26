package com.mycompany.service;

import com.mycompany.dao.TaskDAO;
import com.mycompany.model.Task;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class ReminderService implements Runnable {
    private final TaskDAO taskDAO;
    private volatile boolean running = true;

    public ReminderService() {
        this.taskDAO = new TaskDAO();
    }
    
    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<Task> tasks = taskDAO.findAll();
                LocalDateTime now = LocalDateTime.now();

                for (Task task : tasks) {
                    if (!task.isCompleted() && task.getDueDate() != null) {
                        long minutesUntilDue = ChronoUnit.MINUTES.between(now, task.getDueDate());
                        // Notifikasi jika sisa waktu 5 menit atau kurang, dan lebih dari 0
                        if (minutesUntilDue <= 5 && minutesUntilDue >= 0) {
                            showNotification(task);
                        }
                    }
                }
                
                // Cek setiap 1 menit
                Thread.sleep(60000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Reminder thread interrupted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showNotification(Task task) {
        // Panggil di Event Dispatch Thread agar aman untuk GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            String message = String.format(I18nService.getString("notification.message"), task.getDescription());
            String title = I18nService.getString("notification.title");
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
            playSound();
        });
    }

    // Bagian MULTIMEDIA
    private void playSound() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("notification.wav");
            if (is == null) {
                System.err.println("Warning: notification.wav not found in resources.");
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
