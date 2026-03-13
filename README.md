# Student Registration System

A Java Swing desktop application for registering and managing student records, built as a lab exercise.

## Tools & Technologies

- Apache NetBeans
- Java Swing
- MySQL 8.0
- MySQL Workbench
- JCalendar Library (JDateChooser)

## Project Structure

```
navigationflowjavaswing/
├── images/
├── mysql/
│   └── DataTable.sql
└── src/
    ├── model/
    │   └── User.java
    ├── ui/
    │   ├── MainJFrame.java
    │   ├── Form.java
    │   └── ViewJPanel.java
    └── utility/
        └── DatabaseConnector.java
```

## Database Setup

The application connects to a local MySQL database. The connection is configured in `DatabaseConnector.java`:

- **Host:** localhost:3306
- **Database:** userdb
- **Table:** student

Run the SQL script in `mysql/DataTable.sql` in MySQL Workbench to create the database table:

```sql
USE userdb;

CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INT,
    email VARCHAR(100),
    phone VARCHAR(20),
    hobby VARCHAR(255),
    continent VARCHAR(50),
    date_of_birth VARCHAR(20),
    photo_path VARCHAR(255)
);

SELECT * FROM student;
```

## Features

### Registration Form
- Input fields for first name, last name, age, date of birth, email, phone, continent, and hobby
- Phone number mask formatter
- Date picker with min/max date validation
- Photo upload from local file system
- Field validation with error messages before submission

### View Panel
- Displays all registered students in a table
- Clicking a row populates the detail fields
- Edit button unlocks fields for editing
- Update button saves changes to the database
- Delete button removes a student with a confirmation prompt

## How to Run

1. Open the project in Apache NetBeans
2. Create the `userdb` database in MySQL Workbench and set up the `student` table
3. Update the DB credentials in `DatabaseConnector.java` if needed
4. Add the JCalendar `.jar` to the project libraries
5. Run `MainJFrame.java`
