package co.rufe.rufe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/public/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes Ciudadanos", description = "Endpoints públicos para alertas y SOS comunitarias")
public class PublicReportController {

    // private final PublicReportService reportService;

    @Operation(summary = "Enviar un Reporte SOS de Evento Rápido", description = "Permite enviar lat/lon y una foto (URL) sin autenticación.")
    @PostMapping("/sos")
    public ResponseEntity<Map<String, Object>> reportEmergency(@RequestBody Map<String, Object> payload) {
        log.info("[ALERTA CIUDADANA] Recibido SOS: {}", payload);
        
        // Aquí se insertaría en una tabla temporal "alertas_tempranas" y se 
        // gatillaría una notificación a los administradores del municipio destino.
        
        return ResponseEntity.ok(Map.of(
            "status", "RECIBIDO",
            "folio", "SOS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            "timestamp", LocalDateTime.now()
        ));
    }
}
