package dev.gamified.GamifiedPlatform.config;

import dev.gamified.GamifiedPlatform.deadletter.DeadLetterQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
 * Configuração para execução assíncrona e agendamento de tarefas.
 * Permite que logs de auditoria sejam gravados sem bloquear requisições.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);
    private final DeadLetterQueue deadLetterQueue;

    public AsyncConfig(DeadLetterQueue deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Número de threads que ficam sempre ativas
        executor.setCorePoolSize(5);

        // Número máximo de threads que podem ser criadas
        executor.setMaxPoolSize(10);

        // Capacidade da fila de tarefas pendentes
        executor.setQueueCapacity(100);

        // Prefixo para facilitar debug nos logs
        executor.setThreadNamePrefix("GamifiedAsync-");

        // Aguarda conclusão de tarefas no shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Tempo máximo de espera no shutdown (60 segundos)
        executor.setAwaitTerminationSeconds(60);

        executor.setRejectedExecutionHandler((r, exec) -> {
            log.error("Task rejected, sending to DLQ: {}", r);
            deadLetterQueue.add(r);
        });

        executor.initialize();
        return executor;
    }
}
