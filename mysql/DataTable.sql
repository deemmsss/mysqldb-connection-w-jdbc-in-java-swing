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