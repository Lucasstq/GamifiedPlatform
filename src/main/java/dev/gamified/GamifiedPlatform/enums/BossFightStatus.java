package dev.gamified.GamifiedPlatform.enums;

public enum BossFightStatus {
    LOCKED,              // Boss ainda não está disponível
    UNLOCKED,            // Boss foi desbloqueado (80% de progresso)
    IN_PROGRESS,         // Usuário iniciou o desafio
    AWAITING_EVALUATION, // Usuário submeteu, aguardando avaliação
    DEFEATED,            // Boss foi derrotado (aprovado)
    FAILED               // Falhou na tentativa, pode tentar novamente
}

