package dev.gamified.GamifiedPlatform.constants;

//Constantes numéricas usadas na aplicação.
public final class BusinessConstants {

    private BusinessConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    //Percentual de progresso necessário para desbloquear um boss (80%),
    //O usuário precisa completar 80% das missões de um nível para enfrentar o boss.
    public static final double BOSS_UNLOCK_THRESHOLD_PERCENTAGE = 80.0;

    // Percentual máximo de progresso (100%).
    public static final double MAX_PROGRESS_PERCENTAGE = 100.0;

    //Percentual mínimo de progresso (0%).
    public static final double MIN_PROGRESS_PERCENTAGE = 0.0;

    //Multiplicador para conversão de decimal para percentual (100).
    public static final double PERCENTAGE_MULTIPLIER = 100.0;

    //Valor default quando não há jogadores no ranking.
    public static final double DEFAULT_PERCENTILE = 0.0;

    //Multiplicador para conversão de fração para percentual no ranking.
    public static final double RANKING_PERCENTAGE_MULTIPLIER = 100.0;

    //Taxa de derrota inicial para comparação (100%), Usado para encontrar o boss mais difícil.
    public static final double INITIAL_DEFEAT_RATE = 100.0;

    //Taxa de falha inicial para comparação (0%), Usado para encontrar a missão mais difícil
    public static final double INITIAL_FAILURE_RATE = 0.0;

    //Valor default para taxas quando não há dados (0%).
    public static final double DEFAULT_RATE = 0.0;
}

