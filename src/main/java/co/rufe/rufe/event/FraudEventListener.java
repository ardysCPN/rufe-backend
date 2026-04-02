package co.rufe.rufe.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEventListener {

    // Inject DAO/Repository layer here if we want to query DB:
    // private final IRegistroRufeDao registroRufeDao;

    @Async
    @EventListener
    public void handleFraudDetectionEvent(FraudDetectionEvent event) {
        log.info("[BAÚL GLOBAL] Iniciando escaneo de fraude para Registro UIID: {}", event.getRegistroRufe().getClienteId());
        
        // Simulación: Buscamos en la BD si existe este integrante hogar en OTRA organizacion.
        for(String doc : event.getDocumentosAuditables()) {
            boolean isFraudulent = checkFraudForDocument(doc, event.getRegistroRufe().getOrganizacionId());
            if (isFraudulent) {
                log.warn("[BAÚL GLOBAL] ¡ALERTA! El documento {} ya ha recibido ayudas en otro municipio/organización.", doc);
                // Aquí actualizaríamos el registro:
                // registroRufeDao.markAsFraudulent(event.getRegistroRufe().getId());
            } else {
                log.info("[BAÚL GLOBAL] Documento {} OK. No detectado en multitenant.", doc);
            }
        }
    }

    private boolean checkFraudForDocument(String documento, Long currentOrganizacionId) {
        // Dummy logic (por ahora simula todo OK para no trabar tests):
        // En un entorno real se haría un Count() sobre "integrantes_hogar" cruzado con "registros_rufe"
        // donde organizacionId != currentOrganizacionId
        return false;
    }
}
