package com.mycompany;

import com.mycompany.db.MongoUtil;
import com.mycompany.service.ReminderService;
import com.mycompany.ui.MainFrame;

/**
 *
 * @author ASUS
 */
public class app {
    public static void main(String[] args) {
        // Jalankan Reminder Service di Thread terpisah
        ReminderService reminderService = new ReminderService();
        Thread reminderThread = new Thread(reminderService);
        reminderThread.setDaemon(true); // Agar thread mati saat aplikasi ditutup
        reminderThread.start();
        
        // Jalankan UI di Event Dispatch Thread (EDT)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });

        // Tambahkan shutdown hook untuk menutup koneksi DB dengan bersih
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down... Closing MongoDB connection.");
            reminderService.stop(); // Beri sinyal thread untuk berhenti
            MongoUtil.close();
        }));
    }
}
