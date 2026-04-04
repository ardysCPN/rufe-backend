package co.rufe.rufe.event;

import org.springframework.context.ApplicationEvent;
import co.rufe.rufe.model.RegistroRufe;
import java.util.List;

public class FraudDetectionEvent extends ApplicationEvent {
    
    private final RegistroRufe registroRufe;
    private final List<String> documentosAuditables;

    public FraudDetectionEvent(Object source, RegistroRufe registroRufe, List<String> documentosAuditables) {
        super(source);
        this.registroRufe = registroRufe;
        this.documentosAuditables = documentosAuditables;
    }

    public RegistroRufe getRegistroRufe() {
        return registroRufe;
    }

    public List<String> getDocumentosAuditables() {
        return documentosAuditables;
    }
}
