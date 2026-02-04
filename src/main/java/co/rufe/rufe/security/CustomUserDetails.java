package co.rufe.rufe.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long id;
    private final Long organizacionId;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
            Long id, Long organizacionId) {
        super(username, password, authorities);
        this.id = id;
        this.organizacionId = organizacionId;
    }
}
