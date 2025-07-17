package co.rufe.rufe.service;

import co.rufe.rufe.dto.auth.AuthResponse;
import co.rufe.rufe.dto.auth.LoginRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    // Podríamos añadir register para nuevos usuarios/organizaciones si el caso de uso lo requiere
    // AuthResponse register(RegisterRequest request);
}
