package utility;

import model.User;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author demi
 */

public class DatabaseConnector {

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/userdb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "my-secret-pw";

    public static void addUser(User u) throws SQLException {
        String query = "INSERT INTO student(first_name, last_name, age, email, phone, hobby, continent, date_of_birth, photo_path) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, u.getFirstName());
            stmt.setString(2, u.getLastName());
            stmt.setInt(3, u.getAge());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getPhone());
            stmt.setString(6, u.getHobby());
            stmt.setString(7, u.getContinent());
            stmt.setDate(8, u.getDateOfBirth() != null ?       
                new java.sql.Date(u.getDateOfBirth().getTime()) : null);
            stmt.setString(9, null);
            stmt.executeUpdate();
        }
    }

    public static ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        String query = "SELECT * FROM student";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Date dob = null;
                String dobStr = rs.getString("date_of_birth");
                if (dobStr != null) {
                    try {
                        dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                    } catch (ParseException e) {
                        dob = null;
                    }
                }

                Image photo = null;
                String photoPath = rs.getString("photo_path");
                if (photoPath != null) {
                    try {
                        BufferedImage img = ImageIO.read(new File(photoPath));
                        photo = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    } catch (IOException e) {
                        photo = null;
                    }
                }

               
                // User constructor order: firstName, lastName, age, email, phone, continent, hobby, photo, dob
                User u = new User(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("continent"),  
                    rs.getString("hobby"),      
                    photo,
                    dob
                );
                u.setId(rs.getInt("student_id"));
                list.add(u);
            }
        }
        return list;
    }

    public static void updateUser(User u) throws SQLException {
        String query = "UPDATE student SET first_name=?, last_name=?, age=?, email=?, phone=?, hobby=?, continent=?, date_of_birth=? WHERE student_id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, u.getFirstName());
            stmt.setString(2, u.getLastName());
            stmt.setInt(3, u.getAge());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getPhone());
            stmt.setString(6, u.getHobby());
            stmt.setString(7, u.getContinent());
            stmt.setDate(8, u.getDateOfBirth() != null ?    
                new java.sql.Date(u.getDateOfBirth().getTime()) : null);
            stmt.setInt(9, u.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM student WHERE student_id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}