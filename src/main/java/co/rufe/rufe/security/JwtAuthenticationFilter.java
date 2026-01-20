package co.rufe.rufe.security;

import co.rufe.rufe.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService; // Renombrado para consistencia

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                // Long organizacionId = jwtTokenProvider.getOrganizacionIdFromJwt(token); // Si el organizacionId está en el JWT

                // Cargar UserDetails: Esto es CRUCIAL. Aunque el token tenga claims de autoridades,
                // siempre es mejor cargar los UserDetails frescos de la DB para asegurar que los
                // permisos no estén desactualizados o manipulados, y para establecer el TenantContext.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // El TenantContext.setCurrentOrganizationId() ya debería haber sido llamado
                // dentro de CustomUserDetailsService.loadUserByUsername(username).
                // Si no es el caso, deberías obtener el organizacionId del userDetails o del JWT y establecerlo aquí.
                // Ejemplo: TenantContext.setCurrentOrganizationId(((CustomUserDetails) userDetails).getOrganizacionId());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // La contraseña ya no es necesaria aquí
                        userDetails.getAuthorities() // Usar las autoridades obtenidas de UserDetails (DB)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuario {} autenticado con las autoridades: {}", username,
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
            }
            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Capturar excepciones para que CustomAuthenticationEntryPoint las maneje
            log.error("Error en JwtAuthenticationFilter: {}", ex.getMessage(), ex);
            // Lanzar la excepción para que Spring Security la intercepte y la maneje con AuthenticationEntryPoint
            throw ex;
        } finally {
            // No limpiar el TenantContext aquí si tienes un filtro dedicado (TenantContextCleanupFilter)
            // que se ejecuta DESPUÉS de este filtro en la cadena de seguridad.
            // Si no lo tienes configurado así, deberías limpiar el TenantContext aquí.
            // TenantContext.clear();
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}