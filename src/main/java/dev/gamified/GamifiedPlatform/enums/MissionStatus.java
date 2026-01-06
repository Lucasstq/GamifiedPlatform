package dev.gamified.GamifiedPlatform.enums;

public enum MissionStatus {
    AVAILABLE,           // Missão pode ser iniciada
    IN_PROGRESS,        // Aluno iniciou mas não submeteu
    AWAITING_EVALUATION, // Aluno submeteu, aguardando mentor
    COMPLETED,           // Aprovada pelo mentor
    FAILED            // Reprovada pelo mentor, pode reenviar
}

