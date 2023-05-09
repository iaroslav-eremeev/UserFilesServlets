package model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userFiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "filename"})
})
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "filename")
    @NonNull
    private String filename;
    @Column(name = "serverFilename", unique = true)
    private String serverFilename;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @NonNull
    private User user;

    @Override
    public String toString() {
        return "UserFile{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", serverFilename='" + serverFilename + '\'' +
                ", user=" + user.getLogin() +
                '}';
    }
}
