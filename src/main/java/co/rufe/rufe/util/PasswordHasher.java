package co.rufe.rufe.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashea una contraseña.
     * @param rawPassword La contraseña en texto plano.
     * @return La contraseña hasheada.
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una contraseña hasheada.
     * @param rawPassword La contraseña en texto plano.
     * @param encodedPassword La contraseña hasheada almacenada.
     * @return true si coinciden, false en caso contrario.
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
