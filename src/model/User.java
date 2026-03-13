package model;

import java.awt.Image;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author demi
 */

public class User {
    
    private int id;          
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phone;
    private String continent;
    private String hobby;
    private Image photo;
    private Date dateOfBirth;
    
    public User(String firstName, String lastName, int age, String email, String phone,
                String continent, String hobby, Image photo, Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.continent = continent;
        this.hobby = hobby;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }

    public String getHobby() { return hobby; }
    public void setHobby(String hobby) { this.hobby = hobby; }

    public Image getPhoto() { return photo; }
    public void setPhoto(Image photo) { this.photo = photo; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String dobString = (dateOfBirth != null) ? dateFormat.format(dateOfBirth) : "Not provided";
        return """
               User Details: 
               Name: """ + firstName + " " + lastName + "\n" +
                "Age: " + age + "\n" +
                "Date of Birth: " + dobString + "\n" +
                "Email: " + email + "\n" +
                "Phone: " + phone + "\n" +
                "Hobby: " + hobby + "\n" +
                "Continent: " + continent + "\n";
    }
}