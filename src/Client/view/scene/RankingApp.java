package Client.view.scene;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class RankingApp extends JFrame {
    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private String currentUsername;  // Tên người đăng nhập
    private JLabel lblUserRank;  // Nhãn hiển thị thứ hạng người dùng hiện tại
    private JPanel panelUserInfo; // Panel để hiển thị thông tin cá nhân người dùng
    private server.db.layers.DAL.PlayerDAL playerDAL;
    public RankingApp(String currentUsername) {
        this.currentUsername = currentUsername;  // Lưu tên người đăng nhập

        setTitle("Bảng Xếp Hạng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Đóng cửa sổ khi thoát bảng xếp hạng
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo bảng và mô hình dữ liệu
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Hạng");
        tableModel.addColumn("Tên");
        tableModel.addColumn("Điểm");
        tableModel.addColumn("Số trận");
        tableModel.addColumn("Thắng");
        tableModel.addColumn("Hòa");
        tableModel.addColumn("Thua");

        rankingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel để hiển thị thông tin cá nhân của người dùng
        panelUserInfo = new JPanel();
        panelUserInfo.setLayout(new BoxLayout(panelUserInfo, BoxLayout.Y_AXIS));
        panelUserInfo.setBorder(BorderFactory.createTitledBorder("Thông tin của bạn"));
        lblUserRank = new JLabel();
        panelUserInfo.add(lblUserRank);
        add(panelUserInfo, BorderLayout.SOUTH);

        // Tải dữ liệu bảng xếp hạng
        loadRankingData();

        setVisible(true);
    }

    // Hàm tải dữ liệu bảng xếp hạng từ cơ sở dữ liệu
    private void loadRankingData() {
        try {
            playerDAL = new server.db.layers.DAL.PlayerDAL();
            JSONArray rankings = playerDAL.getRank();
            this.tableModel.setRowCount(0);
            int userRank = -1;
            int rank = 1;
            // Populate table with data
            for (Object elem : rankings) {
                JSONObject player = (JSONObject) elem;
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(player.getInt("rank")));
                row.add(player.getString("name"));
                row.add(String.valueOf(player.getDouble("score")));
                row.add(String.valueOf(player.getInt("matchCount")));
                row.add(String.valueOf(player.getInt("winCount")));
                row.add(String.valueOf(player.getInt("loseCount")));
                this.tableModel.addRow(row);

                // Update user rank if applicable
                if (player.get("name").equals(this.currentUsername)) {
                    userRank = (int) player.get("rank");
                    this.lblUserRank.setText("<html>Tên: " + (String) this.currentUsername + "<br/>Hạng: " + userRank + "<br/>Điểm: " + (double)player.get("score")+ "<br/>Số trận: " + (int)player.get("matchCount") + "<br/>Thắng: " + (int)player.get("winCount")+ "<br/>Thua: " +(int) player.get("loseCount") + "</html>");
                }
                rank++;
            }

            if (userRank == -1) {
                this.lblUserRank.setText("Bạn không có trong bảng xếp hạng.");
            }






        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
