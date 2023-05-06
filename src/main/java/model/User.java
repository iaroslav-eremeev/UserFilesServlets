package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "login", unique = true)
    @NonNull
    private String login;
    @Column(name = "password")
    @NonNull
    private String password;
    @Column(name = "name")
    @NonNull
    private String name;
    @Column(name = "surname")
    @NonNull
    private String surname;

    @OneToMany
    @JsonIgnore
    @ToString.Exclude
    private ArrayList<UserFile> userFiles = new ArrayList<>();

}
