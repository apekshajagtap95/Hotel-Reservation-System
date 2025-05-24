import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HotelReservationSystem {
    private JFrame frame;
    private Connection connection;
    
    class BackgroundPanel extends JPanel{
        private Image backgroundImage;
        private boolean imageLoaded = false;
        
        public BackgroundPanel() {
            try {
                URL imageUrl = getClass().getResource("/hotel1.jpg");
                if (imageUrl != null) {
                    backgroundImage = ImageIO.read(imageUrl);
                    imageLoaded = true;
                    System.out.println("Image loaded successfully: " + imageUrl);
                } else {
                    System.err.println("Image not found at: /Hotel.jpg");
                }
            } catch (IOException e) {
                System.err.println("Error loading image:");
                e.printStackTrace();
            }
            setOpaque(!imageLoaded);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imageLoaded) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
    
    public HotelReservationSystem() {
        initializeDatabase();
        initializeUI();
    }
    private void initializeDatabase() {
        String url = "jdbc:mysql://localhost:3306/hoteldb";
        String username = "root";
        String password = "Apeksha";
        
        try {
            connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            
            // Create rooms table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rooms ("
                + "room_number INT PRIMARY KEY, "
                + "room_type VARCHAR(50) NOT NULL, "
                + "is_ac BOOLEAN NOT NULL, "
                + "is_available BOOLEAN DEFAULT TRUE)");
            
            // Create reservations table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS reservations ("
                + "reservation_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "guest_name VARCHAR(100) NOT NULL, "
                + "room_number INT NOT NULL, "
                + "room_type VARCHAR(50) NOT NULL, "
                + "contact_number VARCHAR(20) NOT NULL, "
                + "payment_method VARCHAR(50) NOT NULL, "
                + "total_price DECIMAL(10,2) NOT NULL, "
                + "nights INT NOT NULL, "
                + "is_ac BOOLEAN NOT NULL, "
                + "reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (room_number) REFERENCES rooms(room_number))");
            
            // Checking room table is empty or not 
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0)
            {
                stmt.executeUpdate("INSERT INTO rooms VALUES (101, 'Standard (Non-AC)', false, true)");
                stmt.executeUpdate("INSERT INTO rooms VALUES (102, 'Standard (AC)', true, true)");
                stmt.executeUpdate("INSERT INTO rooms VALUES (103, 'Deluxe (Non-AC)', false, true)");
                stmt.executeUpdate("INSERT INTO rooms VALUES (104, 'Deluxe (AC)', true, true)");
                stmt.executeUpdate("INSERT INTO rooms VALUES (105, 'Suite (AC)', true, true)");
                System.out.println("Initialized 5 rooms in the database");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            e.printStackTrace(); 
            System.exit(1);
        }
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        Dimension buttonSize = new Dimension(180, 40);
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setBackground(new Color(255, 255, 255, 200));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        return button;
    }

    private void initializeUI() {
        frame = new JFrame("Hotel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
       
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 30));

        JLabel welcomeLabel = new JLabel("Welcome to our Hotel!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JPanel welcomeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomeContainer.setOpaque(false);
        
        JLabel resortLabel = new JLabel("Moonlight Resort");
        resortLabel.setFont(new Font("calibri", Font.BOLD | Font.ITALIC, 28));
        resortLabel.setForeground(new Color(255, 215, 0));
        resortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomeContainer.add(welcomeLabel);
        welcomeContainer.add(Box.createRigidArea(new Dimension(160,80)));
        welcomeContainer.add(resortLabel);
        topPanel.add(welcomeContainer, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalGlue(), BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        menuPanel.setOpaque(false);
        menuPanel.setMaximumSize(new Dimension(400, 400));
    
        JButton reserveButton = createStyledButton("Reserve Room");
        JButton viewButton = createStyledButton("View Reservations");
        JButton findButton = createStyledButton("Find Room Number");
        JButton updateButton = createStyledButton("Update Reservation");
        JButton deleteButton = createStyledButton("Cancel Reservation");
        JButton exitButton = createStyledButton("Exit");
        
        Dimension xlButtonSize = new Dimension(800,50);
        reserveButton.setMaximumSize(xlButtonSize);
        viewButton.setMaximumSize(xlButtonSize);
        findButton.setMaximumSize(xlButtonSize);
        updateButton.setMaximumSize(xlButtonSize);
        deleteButton.setMaximumSize(xlButtonSize);
        exitButton.setMaximumSize(xlButtonSize);
        
        reserveButton.addActionListener(e -> showReservationForm());
        viewButton.addActionListener(e -> viewReservations());
        findButton.addActionListener(e -> findRoomNumber());
        updateButton.addActionListener(e -> updateReservation());
        deleteButton.addActionListener(e -> cancelReservation());
        exitButton.addActionListener(e -> System.exit(0));
        
        menuPanel.add(reserveButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(viewButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(findButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(updateButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(deleteButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(exitButton);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(menuPanel, BorderLayout.NORTH);
        leftPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        backgroundPanel.add(leftPanel, BorderLayout.WEST);
        backgroundPanel.add(welcomeContainer, BorderLayout.CENTER);
             
        frame.setContentPane(backgroundPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
  
    // Check room availability
    private boolean isRoomAvailable(int roomNumber) {
        try {
            // First check if room exists
            String checkSql = "SELECT*FROM rooms WHERE room_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, roomNumber);
            if (!checkStmt.executeQuery().next()) {
                System.out.println("Room " + roomNumber + " doesn't exist");
                return false;
            }
            
            // Then check availability
            String availSql = "SELECT is_available FROM rooms WHERE room_number = ?";
            PreparedStatement availStmt = connection.prepareStatement(availSql);
            availStmt.setInt(1, roomNumber);
            ResultSet rs = availStmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_available");
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  
    private void setRoomAvailability(int roomNumber, boolean isAvailable) {
        try {
            String sql = "UPDATE rooms SET is_available = ? WHERE room_number = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, roomNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating room status: " + e.getMessage());
        }
    }
 // mapping of room prices
    private final Map<String, Integer> ROOM_PRICES = new HashMap<String, Integer>() {{
        put("Standard (Non-AC)", 1000);
        put("Standard (AC)", 1500);
        put("Deluxe (Non-AC)", 1500);
        put("Deluxe (AC)", 2000);
        put("Suite (AC)", 3000);
    }};
    private void updatePrice(JSpinner nightsSpinner, JComboBox<String> roomTypeCombo, JLabel priceLabel) {
        try {
            int nights = (Integer)nightsSpinner.getValue();
            String roomType = (String)roomTypeCombo.getSelectedItem();
            int pricePerNight = ROOM_PRICES.get(roomType);
            int totalPrice = nights * pricePerNight;
            
            if (nights >= 7) {
                totalPrice = (int)(totalPrice * 0.85);
            } else if (nights >= 3) {
                totalPrice = (int)(totalPrice * 0.90);
            }
            
            priceLabel.setText(String.format("₹%d (₹%d/night)", totalPrice, pricePerNight));
        } catch (Exception e) {
            priceLabel.setText("Calculating...");
        }
    }
  //reservation form
    private void showReservationForm() {
    	JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField IdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JComboBox<String> roomTypeCombo = new JComboBox<>(ROOM_PRICES.keySet().toArray(new String[0]));
        JTextField contactField = new JTextField();
        JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "UPI"});
        JSpinner nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        JLabel priceLabel = new JLabel();
        JLabel availabilityLabel = new JLabel("Check availability");
        availabilityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        
        JButton checkAvailButton = new JButton("Check Availability");
        checkAvailButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                boolean available = isRoomAvailable(roomNumber);
                System.out.println("Checking room " + roomNumber + ", available: " + available); // Debug line
                if (available) {
                    availabilityLabel.setText("Status: AVAILABLE");
                    availabilityLabel.setForeground(Color.GREEN);
                } else {
                    availabilityLabel.setText("Status: NOT AVAILABLE");
                    availabilityLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                availabilityLabel.setText("Invalid room number");
                availabilityLabel.setForeground(Color.RED);
            }
        });

        roomTypeCombo.addActionListener(e -> updatePrice(nightsSpinner, roomTypeCombo, priceLabel));
        nightsSpinner.addChangeListener(e -> updatePrice(nightsSpinner, roomTypeCombo, priceLabel));
        updatePrice(nightsSpinner, roomTypeCombo, priceLabel);
        panel.add(new JLabel("Reservation Id:"));
        panel.add(IdField);
        panel.add(new JLabel("Guest Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        panel.add(new JLabel(""));
        panel.add(checkAvailButton);
        panel.add(new JLabel("Availability:"));
        panel.add(availabilityLabel);
        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeCombo);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);
        panel.add(new JLabel("Nights Staying:"));
        panel.add(nightsSpinner);
        panel.add(new JLabel("Total Price:"));
        panel.add(priceLabel);
        panel.add(new JLabel("Payment Method:"));
        panel.add(paymentCombo);

        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Reserve a Room", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                
                // Check availability before reserving
                if (!isRoomAvailable(roomNumber)) {
                    JOptionPane.showMessageDialog(frame, "Room " + roomNumber + " is not available!");
                    return;
                }
                String reservationId=IdField.getText();
                String guestName = nameField.getText();
                String roomType = (String)roomTypeCombo.getSelectedItem();
                String contactNumber = contactField.getText();
                String paymentMethod = (String)paymentCombo.getSelectedItem();
                int nights = (Integer)nightsSpinner.getValue();
                
                int pricePerNight = ROOM_PRICES.get(roomType);
                int totalPrice = nights * pricePerNight;
                if (nights >= 7) totalPrice = (int)(totalPrice * 0.85);
                else if (nights >= 3) totalPrice = (int)(totalPrice * 0.90);
                
                // Start transaction
                connection.setAutoCommit(false);
                
                try {
                    // Insert reservation details into reservation table
                    String sql = "INSERT INTO reservations (reservation_id,guest_name, room_number, room_type, " +
                               "contact_number, payment_method, total_price, nights, is_ac) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, reservationId);
                    stmt.setString(2, guestName);
                    stmt.setInt(3, roomNumber);
                    stmt.setString(4, roomType);
                    stmt.setString(5, contactNumber);
                    stmt.setString(6, paymentMethod);
                    stmt.setInt(7, totalPrice);
                    stmt.setInt(8, nights);
                    stmt.setBoolean(9, roomType.contains("AC"));
                    stmt.executeUpdate();
                    
                    setRoomAvailability(roomNumber, false);
                    
                    connection.commit();
                    JOptionPane.showMessageDialog(frame, "Reservation successful!");
                } catch (SQLException e) {
                    connection.rollback();
                    JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for room and nights");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Transaction error: " + e.getMessage());
            }
        }
    }
    //view reservations
    
    private void viewReservations() {
        try {
            JTabbedPane tabbedPane = new JTabbedPane();
            
            JPanel reservationsPanel = new JPanel(new BorderLayout());
            String sql = "SELECT r.reservation_id, r.guest_name, r.room_number, r.room_type, " +
                       "r.contact_number, r.payment_method, r.total_price, r.nights, " +
                       "r.reservation_date, m.is_available " +
                       "FROM reservations r " +
                       "JOIN rooms m ON r.room_number = m.room_number";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
            	//create html table
                StringBuilder sb = new StringBuilder();
                sb.append("<html><table border='1'><tr>")
                  .append("<th>ID</th><th>Guest</th><th>Room</th><th>Type</th><th>Contact</th>")
                  .append("<th>Payment</th><th>Price</th><th>Nights</th><th>Date</th></tr>");
                
                while (rs.next()) {
                    sb.append("<tr>")
                      .append("<td>").append(rs.getInt("reservation_id")).append("</td>")
                      .append("<td>").append(rs.getString("guest_name")).append("</td>")
                      .append("<td>").append(rs.getInt("room_number")).append("</td>")
                      .append("<td>").append(rs.getString("room_type")).append("</td>")
                      .append("<td>").append(rs.getString("contact_number")).append("</td>")
                      .append("<td>").append(rs.getString("payment_method")).append("</td>")
                      .append("<td>").append(rs.getInt("total_price")).append("</td>")
                      .append("<td>").append(rs.getInt("nights")).append("</td>")
                      .append("<td>").append(rs.getTimestamp("reservation_date")).append("</td>")
                      .append("</tr>");
                }
                
                sb.append("</table></html>");
                
                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setText(sb.toString());
                textPane.setEditable(false);
                
                JScrollPane scrollPane = new JScrollPane(textPane);
                scrollPane.setPreferredSize(new Dimension(1000, 400));
                reservationsPanel.add(scrollPane, BorderLayout.CENTER);
            }
            
            JPanel roomAvailabilityPanel = new JPanel(new BorderLayout());
            String roomSql = "SELECT room_number, room_type, is_available FROM rooms WHERE room_number BETWEEN 101 AND 105 ORDER BY room_number";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(roomSql)) {
                
                StringBuilder sb = new StringBuilder();
                sb.append("<html><table border='1'><tr>")
                  .append("<th>Room Number</th><th>Room Type</th><th>Availability</th></tr>");
                
                while (rs.next()) {
                    sb.append("<tr>")
                      .append("<td>").append(rs.getInt("room_number")).append("</td>")
                      .append("<td>").append(rs.getString("room_type")).append("</td>")
                      .append("<td style='color:").append(rs.getBoolean("is_available") ? "green'>AVAILABLE (YES)" : "red'>OCCUPIED (NO)").append("</td>")
                      .append("</tr>");
                }
                
                sb.append("</table></html>");
                
                JTextPane roomTextPane = new JTextPane();
                roomTextPane.setContentType("text/html");
                roomTextPane.setText(sb.toString());
                roomTextPane.setEditable(false);
                
                JScrollPane roomScrollPane = new JScrollPane(roomTextPane);
                roomScrollPane.setPreferredSize(new Dimension(500, 200));
                roomAvailabilityPanel.add(roomScrollPane, BorderLayout.CENTER);
            }
            
            tabbedPane.addTab("Reservations", reservationsPanel);
            tabbedPane.addTab("Room Availability (101-105)", roomAvailabilityPanel);
            
            JOptionPane.showMessageDialog(frame, tabbedPane, "Hotel Information", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
        }
    }
    //find room number
    private void findRoomNumber() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        
        panel.add(new JLabel("Reservation ID:"));
        panel.add(idField);
        panel.add(new JLabel("Guest Name:"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Find Room Number", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int reservationId = Integer.parseInt(idField.getText());
                String guestName = nameField.getText();
                
                String sql = "SELECT r.room_number, m.is_available " +
                             "FROM reservations r " +
                             "JOIN rooms m ON r.room_number = m.room_number " +
                             "WHERE r.reservation_id = ? AND r.guest_name = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, reservationId);
                    stmt.setString(2, guestName);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        String availability = rs.getBoolean("is_available") ? "AVAILABLE" : "OCCUPIED";
                        JOptionPane.showMessageDialog(frame, 
                            "Room number for reservation ID " + reservationId + 
                            " is: " + rs.getInt("room_number") + 
                            "\nCurrent status: " + availability);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Reservation not found");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid reservation ID");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
            }
        }
    }
    //update reservation
    
    private void updateReservation() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JComboBox<String> roomTypeCombo = new JComboBox<>(ROOM_PRICES.keySet().toArray(new String[0]));
        JTextField contactField = new JTextField();
        JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "UPI"});
        JSpinner nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        JLabel priceLabel = new JLabel();
        JLabel availabilityLabel = new JLabel("Check new room availability");
        availabilityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add check availability button
        JButton checkAvailButton = new JButton("Check Availability");
        checkAvailButton.addActionListener(e -> {
            try {
                int roomNumber = Integer.parseInt(roomField.getText());
                if (isRoomAvailable(roomNumber)) {
                    availabilityLabel.setText("Status: AVAILABLE");
                    availabilityLabel.setForeground(Color.GREEN);
                } else {
                    availabilityLabel.setText("Status: NOT AVAILABLE");
                    availabilityLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                availabilityLabel.setText("Invalid room number");
                availabilityLabel.setForeground(Color.RED);
            }
        });

        roomTypeCombo.addActionListener(e -> updatePrice(nightsSpinner, roomTypeCombo, priceLabel));
        nightsSpinner.addChangeListener(e -> updatePrice(nightsSpinner, roomTypeCombo, priceLabel));
        updatePrice(nightsSpinner, roomTypeCombo, priceLabel);

        panel.add(new JLabel("Reservation ID to Update:"));
        panel.add(idField);
        panel.add(new JLabel("Guest Name:"));
        panel.add(nameField);
        panel.add(new JLabel("New Room Number:"));
        panel.add(roomField);
        panel.add(new JLabel(""));
        panel.add(checkAvailButton);
        panel.add(new JLabel("Availability:"));
        panel.add(availabilityLabel);
        panel.add(new JLabel("New Room Type:"));
        panel.add(roomTypeCombo);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);
        panel.add(new JLabel("Nights Staying:"));
        panel.add(nightsSpinner);
        panel.add(new JLabel("Total Price:"));
        panel.add(priceLabel);
        panel.add(new JLabel("Payment Method:"));
        panel.add(paymentCombo);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Update Reservation", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int reservationId = Integer.parseInt(idField.getText());
                String guestName = nameField.getText();
                int newRoomNumber = Integer.parseInt(roomField.getText());
                String roomType = (String)roomTypeCombo.getSelectedItem();
                String contactNumber = contactField.getText();
                String paymentMethod = (String)paymentCombo.getSelectedItem();
                int nights = (Integer)nightsSpinner.getValue();
                
                int pricePerNight = ROOM_PRICES.get(roomType);
                int totalPrice = nights * pricePerNight;
                if (nights >= 7) totalPrice = (int)(totalPrice * 0.85);
                else if (nights >= 3) totalPrice = (int)(totalPrice * 0.90);
                
                // Start transaction
                connection.setAutoCommit(false);
                
                try {
                    // First get current room number
                    String getCurrentRoomSql = "SELECT room_number FROM reservations WHERE reservation_id = ?";
                    PreparedStatement getCurrentRoomStmt = connection.prepareStatement(getCurrentRoomSql);
                    getCurrentRoomStmt.setInt(1, reservationId);
                    ResultSet rs = getCurrentRoomStmt.executeQuery();
                    
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(frame, "Reservation not found");
                        return;
                    }
                    
                    int currentRoomNumber = rs.getInt("room_number");
                    
                    // Check if new room is available
                    if (newRoomNumber != currentRoomNumber && !isRoomAvailable(newRoomNumber)) {
                        JOptionPane.showMessageDialog(frame, "New room " + newRoomNumber + " is not available!");
                        return;
                    }
                    
                    // Update reservation in database
                    String updateSql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ?, " +
                                     "room_type = ?, payment_method = ?, total_price = ?, nights = ?, is_ac = ? " +
                                     "WHERE reservation_id = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setString(1, guestName);
                    updateStmt.setInt(2, newRoomNumber);
                    updateStmt.setString(3, contactNumber);
                    updateStmt.setString(4, roomType);
                    updateStmt.setString(5, paymentMethod);
                    updateStmt.setInt(6, totalPrice);
                    updateStmt.setInt(7, nights);
                    updateStmt.setBoolean(8, roomType.contains("AC"));
                    updateStmt.setInt(9, reservationId);
                    updateStmt.executeUpdate();
                    
                    // Update room availability status if room number changed
                    if (newRoomNumber != currentRoomNumber) {
                        
                        setRoomAvailability(currentRoomNumber, true);
                        
                        setRoomAvailability(newRoomNumber, false);
                    }
                    
                    connection.commit();
                    JOptionPane.showMessageDialog(frame, "Reservation updated successfully!");
                } catch (SQLException e) {
                    connection.rollback();
                    JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for ID, room number, and nights");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Transaction error: " + e.getMessage());
            }
        }
    }
//cancel reservation
    
    private void cancelReservation() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        
        panel.add(new JLabel("Reservation ID:"));
        panel.add(idField);
        panel.add(new JLabel("Guest Name (for verification):"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Cancel Reservation", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int reservationId = Integer.parseInt(idField.getText());
                String guestName = nameField.getText();
                
                // Start transaction
                connection.setAutoCommit(false);
                
                try {
                    // First get reservation details
                    String selectSql = "SELECT r.room_number, r.payment_method, r.total_price " +
                                     "FROM reservations r " +
                                     "WHERE r.reservation_id = ? AND r.guest_name = ?";
                    PreparedStatement selectStmt = connection.prepareStatement(selectSql);
                    selectStmt.setInt(1, reservationId);
                    selectStmt.setString(2, guestName);
                    ResultSet rs = selectStmt.executeQuery();
                    
                    if (rs.next()) {
                        int roomNumber = rs.getInt("room_number");
                        String paymentMethod = rs.getString("payment_method");
                        double amount = rs.getDouble("total_price");
                        
                        //create confirm box to confirm user cancel reservation or not
                        int confirm = JOptionPane.showConfirmDialog(frame, 
                            "This reservation was paid via " + paymentMethod + " for ₹" + amount + 
                            "\nAre you sure you want to cancel?", 
                            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                        
                        //if user select yes option then delete reservation
                        if (confirm == JOptionPane.YES_OPTION) {
                            // Delete reservation
                            String deleteSql = "DELETE FROM reservations WHERE reservation_id = ? AND guest_name = ?";
                            PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                            deleteStmt.setInt(1, reservationId);
                            deleteStmt.setString(2, guestName);
                            deleteStmt.executeUpdate();
                            
                        
                            setRoomAvailability(roomNumber, true);
                            
                            connection.commit();
                            JOptionPane.showMessageDialog(frame, "Reservation cancelled successfully! Refund processed to " + paymentMethod);
                        } else {
                            connection.rollback();
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "No matching reservation found");
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid reservation ID");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Transaction error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new HotelReservationSystem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
