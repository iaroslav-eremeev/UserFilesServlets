package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserFile> userFiles = new ArrayList<>();

}
