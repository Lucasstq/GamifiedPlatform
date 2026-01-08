package dev.gamified.GamifiedPlatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * Configuração para execução assíncrona e agendamento de tarefas.
 * Permite que logs de auditoria sejam gravados sem bloquear requisições.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
}

