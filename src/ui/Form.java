/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import java.util.Date;
import java.util.Calendar;

import model.User;
import javax.swing.SpinnerNumberModel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
/**
 *
 * @author demi
 */
public class Form extends javax.swing.JPanel {
    
    private Image selectedPhoto;
    private User currentUser;

    /**
     * Creates new form Form
     */
    public Form() {
        initComponents();
        setUpComponents();
    }
    
    private void setUpComponents() {
        SpinnerNumberModel ageModel = new SpinnerNumberModel(18, 0, 120, 1);
        AgeSpinner.setModel(ageModel);
        
        try {
            MaskFormatter phoneFormatter = new MaskFormatter("(###) ###-####");
            phoneFormatter.setPlaceholderCharacter('_');
            PhoneFormattedTextField.setFormatterFactory(
                new javax.swing.text.DefaultFormatterFactory(phoneFormatter)
            );
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
    // Continent
    String[] continents = {"Select Continent", "Africa", "Asia", "Australia", "Antarctica", "Europe",
                                "North America", "South America"};
    
        ContinentComboBox.setModel(new DefaultComboBoxModel<>(continents));
        
        // Configure JDateChooser
        jDateChooser1.setMaxSelectableDate(new Date());
        Calendar minDate = Calendar.getInstance();
        minDate.set(1900, 0, 1);
        jDateChooser1.setMinSelectableDate(minDate.getTime());
        
        // Buttons
        UploadButton.addActionListener(e -> uploadPhoto());
        SubmitButton.addActionListener(e -> registerUser());
    }
    
    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage img = ImageIO.read(selectedFile);
                Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                selectedPhoto = scaledImg;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading image: " + ex.getMessage(),
                        "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void registerUser() {
        if (!validateFields()) {
            return;
        }
        String firstName = FirstNameTextField.getText().trim();
        String lastName = LastNameTextField.getText().trim();
        int age = (Integer) AgeSpinner.getValue();
        String email = EmailFormattedTextField.getText().trim();
        String phone = PhoneFormattedTextField.getText().trim();
        String continent = (String) ContinentComboBox.getSelectedItem();
        String hobby = ((JTextArea)jScrollPane1.getViewport().getView()).getText().trim();
        Date dateOfBirth = jDateChooser1.getDate();
        
        currentUser = new User(firstName, lastName, age, email, phone,
                            continent, hobby, selectedPhoto, dateOfBirth);
        JOptionPane.showMessageDialog(this, 
                        currentUser.toString(),
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
        java.awt.Container parent = this.getParent();
        while (parent != null && !(parent instanceof MainJFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainJFrame) {
            ((MainJFrame) parent).switchToView(currentUser);
        }
        try {
           utility.DatabaseConnector.addUser(currentUser);
           JOptionPane.showMessageDialog(this, currentUser.toString(),
            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this,
            "Error saving to database: " + e.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (FirstNameTextField.getText().trim().isEmpty())
                errors.append("First Name is required \n");
        else if (!FirstNameTextField.getText().trim().matches("[a-zA-Z]+"))
                errors.append("First Name should contain only letters \n");
                
            if (LastNameTextField.getText().trim().isEmpty())
                 errors.append("Last Name is required \n");
            else if (!LastNameTextField.getText().trim().matches("[a-zA-Z]+"))
                 errors.append("Last Name should contain only letters \n");
                
                    int age = (Integer) AgeSpinner.getValue();
            if (age < 1 || age > 120)
                errors.append("Age must be between 1 and 120\n");
            
            Date dateOfBirth = jDateChooser1.getDate();
            if (dateOfBirth == null) {
                errors.append("Date of Birth is required\n");
            } else {
                Date currentDate = new Date();
            if (dateOfBirth.after(currentDate)) {
                errors.append("Date of Birth cannot be in the future\n");
            }
    
            Calendar minDate = Calendar.getInstance();
                minDate.set(1900, 0, 1);
            if (dateOfBirth.before(minDate.getTime())) {
                errors.append("Date of Birth must be after January 1, 1900\n");
            }
            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);
            Calendar today = Calendar.getInstance();
            int calculatedAge = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                calculatedAge--;
            }
    
            int enteredAge = (Integer) AgeSpinner.getValue();
            if (Math.abs(calculatedAge - enteredAge) > 0) {
                errors.append("Age does not match the Date of Birth\n");
            }
        }
            String email = EmailFormattedTextField.getText().trim();
            if (email.isEmpty())
                errors.append("Email is required\n");
            else if (!isValidEmail(email))
                errors.append("Email format is invalid\n");
            
            String phone = PhoneFormattedTextField.getText().trim();
            if (phone.contains("_") || phone.isEmpty())
                errors.append("Phone number is incomplete\n");
            
            if (ContinentComboBox.getSelectedIndex() == 0)
                errors.append("Please select a continent\n");
            
            if (HobbyTextArea.getText().trim().isEmpty())
                errors.append("Hobby is required\n");
            
            if (selectedPhoto == null) 
                errors.append("Please upload a photo\n");
            
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, "Please fix the following errors: \n" + errors.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        StudentRegistration = new javax.swing.JLabel();
        FirstName = new javax.swing.JLabel();
        LastName = new javax.swing.JLabel();
        Age = new javax.swing.JLabel();
        Email = new javax.swing.JLabel();
        Phone = new javax.swing.JLabel();
        Hobby = new javax.swing.JLabel();
        Continent = new javax.swing.JLabel();
        FirstNameTextField = new javax.swing.JTextField();
        LastNameTextField = new javax.swing.JTextField();
        AgeSpinner = new javax.swing.JSpinner();
        EmailFormattedTextField = new javax.swing.JFormattedTextField();
        PhoneFormattedTextField = new javax.swing.JFormattedTextField();
        ContinentComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        HobbyTextArea = new javax.swing.JTextArea();
        UploadButton = new javax.swing.JButton();
        SubmitButton = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();

        StudentRegistration.setText("Student Registration");

        FirstName.setText("First Name:");

        LastName.setText("Last Name:");

        Age.setText("Age:");

        Email.setText("Email:");

        Phone.setText("Phone:");

        Hobby.setText("Hobby:");

        Continent.setText("Continent:");

        LastNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LastNameTextFieldActionPerformed(evt);
            }
        });

        ContinentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Africa", "Asia", "Australia", "Antarctica", "Europe", "North America", "South America" }));
        ContinentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContinentComboBoxActionPerformed(evt);
            }
        });

        HobbyTextArea.setColumns(20);
        HobbyTextArea.setRows(5);
        jScrollPane1.setViewportView(HobbyTextArea);

        UploadButton.setText("Upload");

        SubmitButton.setText("Submit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(FirstName)
                        .addGap(18, 18, 18)
                        .addComponent(FirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LastName)
                            .addComponent(Age)
                            .addComponent(Email)
                            .addComponent(Phone)
                            .addComponent(Continent)
                            .addComponent(Hobby))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(LastNameTextField)
                                .addContainerGap(453, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AgeSpinner)
                                .addContainerGap(453, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(EmailFormattedTextField)
                                .addContainerGap(453, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(PhoneFormattedTextField)
                                .addContainerGap(453, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ContinentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap(398, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(213, Short.MAX_VALUE))))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(223, 223, 223)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(UploadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SubmitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(249, 249, 249)
                        .addComponent(StudentRegistration)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(StudentRegistration)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FirstName)
                    .addComponent(FirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LastName)
                    .addComponent(LastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Age)
                        .addGap(18, 18, 18)
                        .addComponent(Email)
                        .addGap(18, 18, 18)
                        .addComponent(Phone))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AgeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(EmailFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PhoneFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Continent)
                    .addComponent(ContinentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(Hobby))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(49, 49, 49)
                .addComponent(UploadButton)
                .addGap(37, 37, 37)
                .addComponent(SubmitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void LastNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LastNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LastNameTextFieldActionPerformed

    private void ContinentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinentComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ContinentComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Age;
    private javax.swing.JSpinner AgeSpinner;
    private javax.swing.JLabel Continent;
    private javax.swing.JComboBox<String> ContinentComboBox;
    private javax.swing.JLabel Email;
    private javax.swing.JFormattedTextField EmailFormattedTextField;
    private javax.swing.JLabel FirstName;
    private javax.swing.JTextField FirstNameTextField;
    private javax.swing.JLabel Hobby;
    private javax.swing.JTextArea HobbyTextArea;
    private javax.swing.JLabel LastName;
    private javax.swing.JTextField LastNameTextField;
    private javax.swing.JLabel Phone;
    private javax.swing.JFormattedTextField PhoneFormattedTextField;
    private javax.swing.JLabel StudentRegistration;
    private javax.swing.JButton SubmitButton;
    private javax.swing.JButton UploadButton;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
