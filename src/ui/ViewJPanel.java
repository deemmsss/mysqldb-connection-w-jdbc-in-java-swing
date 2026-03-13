package ui;

import model.User;
import utility.DatabaseConnector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author demi
 */


public class ViewJPanel extends javax.swing.JPanel {

    private JLabel photoDisplayLabel;
    private List<User> userList = new ArrayList<>();

    public ViewJPanel() {
        initComponents();
        setupComponents();
    }

    private void setupComponents() {
        lockFields();

        photoDisplayLabel = new JLabel("No Photo");
        photoDisplayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name", "Age", "Date of Birth", "Email", "Phone", "Continent", "Hobby"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        DataTable.setModel(tableModel);
        DataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadUsers();

        DataTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = DataTable.getSelectedRow();
                if (row >= 0 && row < userList.size()) {
                    displayUser(userList.get(row));
                }
            }
        });

        EditButton.addActionListener(e -> {
            int row = DataTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student to edit.");
                return;
            }
            unlockFields();
        });

        UpdateButton.addActionListener(e -> {
            int row = DataTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student to update.");
                return;
            }
            try {
                User u = userList.get(row);
                u.setFirstName(FirstNameTextField.getText().trim());
                u.setLastName(LastNameTextField.getText().trim());
                u.setAge((Integer) AgeSpinner.getValue());
                u.setEmail(EmailFormattedTextField.getText().trim());
                u.setPhone(PhoneFormattedTextField.getText().trim());
                u.setContinent((String) ContinentComboBox.getSelectedItem());
                u.setHobby(HobbyTextArea.getText().trim());
                u.setDateOfBirth(DOBDateChooser.getDate());
                DatabaseConnector.updateUser(u);
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                loadUsers();
                lockFields();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error updating student: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        DeleteButton.addActionListener(e -> {
            int row = DataTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this student?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DatabaseConnector.deleteUser(userList.get(row).getId());
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                    loadUsers();
                    clearFields();
                    lockFields();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting student: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void loadUsers() {
        try {
            userList = DatabaseConnector.getUsers();
            DefaultTableModel model = (DefaultTableModel) DataTable.getModel();
            model.setRowCount(0);
            for (User u : userList) {
                model.addRow(new Object[]{
                    u.getId(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getAge(),
                    u.getDateOfBirth() != null ?
                        new SimpleDateFormat("MM/dd/yyyy").format(u.getDateOfBirth()) : "",
                    u.getEmail(),
                    u.getPhone(),
                    u.getContinent(),
                    u.getHobby()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void unlockFields() {
        FirstNameTextField.setEnabled(true);
        LastNameTextField.setEnabled(true);
        AgeSpinner.setEnabled(true);
        PhoneFormattedTextField.setEnabled(true);
        EmailFormattedTextField.setEnabled(true);
        ContinentComboBox.setEnabled(true);
        DOBDateChooser.setEnabled(true);
        DOBDateChooser.getCalendarButton().setEnabled(true);
        HobbyTextArea.setEditable(true);
    }

    private void lockFields() {
        FirstNameTextField.setEnabled(false);
        LastNameTextField.setEnabled(false);
        AgeSpinner.setEnabled(false);
        PhoneFormattedTextField.setEnabled(false);
        EmailFormattedTextField.setEnabled(false);
        ContinentComboBox.setEnabled(false);
        DOBDateChooser.setEnabled(false);
        DOBDateChooser.getCalendarButton().setEnabled(false);

        Color grayBg = new Color(240, 240, 240);
        JTextArea hobbyArea = (JTextArea) jScrollPane1.getViewport().getView();
        hobbyArea.setEditable(false);
        hobbyArea.setBackground(grayBg);
        FirstNameTextField.setBackground(grayBg);
        LastNameTextField.setBackground(grayBg);
        AgeSpinner.setBackground(grayBg);
        PhoneFormattedTextField.setBackground(grayBg);
        EmailFormattedTextField.setBackground(grayBg);
        ContinentComboBox.setBackground(grayBg);
        DOBDateChooser.setBackground(grayBg);
    }

    public void displayUser(User user) {
        if (user == null) {
            clearFields();
            return;
        }
        FirstNameTextField.setText(user.getFirstName());
        LastNameTextField.setText(user.getLastName());
        AgeSpinner.setValue(user.getAge());
        PhoneFormattedTextField.setText(user.getPhone());
        EmailFormattedTextField.setText(user.getEmail());
        ContinentComboBox.setSelectedItem(user.getContinent());
        DOBDateChooser.setDate(user.getDateOfBirth());
        ((JTextArea) jScrollPane1.getViewport().getView()).setText(user.getHobby());

        if (photoDisplayLabel != null) {
            if (user.getPhoto() != null) {
                photoDisplayLabel.setIcon(new ImageIcon(user.getPhoto()));
                photoDisplayLabel.setText("");
            } else {
                photoDisplayLabel.setIcon(null);
                photoDisplayLabel.setText("No Photo");
            }
        }
    }

    private void clearFields() {
        FirstNameTextField.setText("");
        LastNameTextField.setText("");
        AgeSpinner.setValue(0);
        PhoneFormattedTextField.setText("");
        EmailFormattedTextField.setText("");
        ContinentComboBox.setSelectedItem(null);
        DOBDateChooser.setDate(null);
        ((JTextArea) jScrollPane1.getViewport().getView()).setText("");
        if (photoDisplayLabel != null) {
            photoDisplayLabel.setIcon(null);
            photoDisplayLabel.setText("No Photo");
        }
    }
     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        StudentDetails = new javax.swing.JLabel();
        FirstName = new javax.swing.JLabel();
        LastName = new javax.swing.JLabel();
        Age = new javax.swing.JLabel();
        Phone = new javax.swing.JLabel();
        Continent = new javax.swing.JLabel();
        Hobby = new javax.swing.JLabel();
        FirstNameTextField = new javax.swing.JTextField();
        LastNameTextField = new javax.swing.JTextField();
        AgeSpinner = new javax.swing.JSpinner();
        PhoneFormattedTextField = new javax.swing.JFormattedTextField();
        ContinentComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        HobbyTextArea = new javax.swing.JTextArea();
        Email = new javax.swing.JLabel();
        EmailFormattedTextField = new javax.swing.JFormattedTextField();
        DOBDateChooser = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        DataTable = new javax.swing.JTable();
        EditButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        UpdateButton = new javax.swing.JButton();

        StudentDetails.setText("Student Details");

        FirstName.setText("First Name:");

        LastName.setText("Last Name:");

        Age.setText("Age:");

        Phone.setText("Phone:");

        Continent.setText("Continent:");

        Hobby.setText("Hobby:");

        FirstNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FirstNameTextFieldActionPerformed(evt);
            }
        });

        PhoneFormattedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhoneFormattedTextFieldActionPerformed(evt);
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

        Email.setText("Email:");

        EmailFormattedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmailFormattedTextFieldActionPerformed(evt);
            }
        });

        DataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "First Name", "Last Name", "Age", "Date of Birth", "Email", "Phone", "Continent", "Hobby"
            }
        ));
        jScrollPane2.setViewportView(DataTable);

        EditButton.setBackground(new java.awt.Color(51, 51, 255));
        EditButton.setText("Edit");
        EditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditButtonActionPerformed(evt);
            }
        });

        DeleteButton.setBackground(new java.awt.Color(255, 51, 51));
        DeleteButton.setText("Delete");

        UpdateButton.setBackground(new java.awt.Color(0, 153, 51));
        UpdateButton.setText("Update");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(308, 308, 308)
                        .addComponent(StudentDetails))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(FirstName)
                                .addGap(18, 18, 18)
                                .addComponent(FirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LastName)
                                    .addComponent(Age, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Email)
                                    .addComponent(Phone)
                                    .addComponent(Continent)
                                    .addComponent(Hobby))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ContinentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(PhoneFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(EmailFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AgeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(LastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DOBDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(EditButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(DeleteButton))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(115, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(UpdateButton)
                .addGap(352, 352, 352))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(StudentDetails)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditButton)
                    .addComponent(DeleteButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FirstName)
                            .addComponent(FirstNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LastName)
                            .addComponent(LastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Age)
                            .addComponent(AgeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DOBDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EmailFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Email))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Phone)
                            .addComponent(PhoneFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Continent)
                            .addComponent(ContinentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Hobby)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(UpdateButton)
                .addContainerGap(468, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void FirstNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FirstNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FirstNameTextFieldActionPerformed

    private void PhoneFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PhoneFormattedTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PhoneFormattedTextFieldActionPerformed

    private void EmailFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmailFormattedTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EmailFormattedTextFieldActionPerformed

    private void ContinentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinentComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ContinentComboBoxActionPerformed

    private void EditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Age;
    private javax.swing.JSpinner AgeSpinner;
    private javax.swing.JLabel Continent;
    private javax.swing.JComboBox<String> ContinentComboBox;
    private com.toedter.calendar.JDateChooser DOBDateChooser;
    private javax.swing.JTable DataTable;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton EditButton;
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
    private javax.swing.JLabel StudentDetails;
    private javax.swing.JButton UpdateButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}