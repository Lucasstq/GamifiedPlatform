-- Migration: Create Security Audit Log Table
-- Description: Tabela para rastrear eventos de segurança
-- Date: 2026-01-07

CREATE TABLE tb_security_audit_log (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    username VARCHAR(100),
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(500),
    details VARCHAR(2000),
    severity VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id)
        REFERENCES tb_user(id) ON DELETE SET NULL
);

-- Índices para melhor performance em queries de auditoria
CREATE INDEX idx_audit_user_id ON tb_security_audit_log(user_id);
CREATE INDEX idx_audit_event_type ON tb_security_audit_log(event_type);
CREATE INDEX idx_audit_timestamp ON tb_security_audit_log(timestamp);
CREATE INDEX idx_audit_severity ON tb_security_audit_log(severity);
CREATE INDEX idx_audit_ip_address ON tb_security_audit_log(ip_address);

-- Comentários
COMMENT ON TABLE tb_security_audit_log IS 'Logs de auditoria de eventos de segurança';
COMMENT ON COLUMN tb_security_audit_log.event_type IS 'Tipo do evento: LOGIN_SUCCESS, LOGIN_FAILED, PASSWORD_CHANGED, etc';
COMMENT ON COLUMN tb_security_audit_log.severity IS 'Nível de severidade: INFO, WARNING, CRITICAL';
COMMENT ON COLUMN tb_security_audit_log.ip_address IS 'Endereço IP de origem do evento';
COMMENT ON COLUMN tb_security_audit_log.details IS 'Detalhes adicionais sobre o evento';

