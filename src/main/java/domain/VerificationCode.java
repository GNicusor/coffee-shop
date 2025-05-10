package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class VerificationCode {
    @Id
    @GeneratedValue
    private Long id;

    private String code;
    private LocalDateTime expiration;

    @OneToOne
    private User user;

    public VerificationCode() {};

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

