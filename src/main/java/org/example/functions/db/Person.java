package org.example.functions.db;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "persons")
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Id")
    private Long Id;

    @Column(name = "FirstName", nullable=false, length = 40)
    @JsonProperty("FirstName")
    private String FirstName;

    @Column(name = "LastName", nullable=false, length = 40)
    @JsonProperty("LastName")
    private String LastName;

    public Person() {}

    public Person(String firstName, String lastName) {
        this.FirstName = firstName;
        this.LastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + Id +
                ", firstName='" + FirstName + '\'' +
                ", lastName='" + LastName + '\'' +
                '}';
    }
}
