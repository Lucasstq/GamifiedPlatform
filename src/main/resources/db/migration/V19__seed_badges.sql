-- Migration V19: Seed epic badges (one per level defeated)

-- Badge 1: Nível Iniciante (Aprendiz do Código)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 1),
    'Vencedor da Sintaxe',
    'Mestre dos Fundamentos',
    'Concedido aos valentes que derrotaram o temível Syntax Sentinel. Este badge representa o domínio completo dos fundamentos da programação: variáveis, condicionais, loops e funções. Aqueles que o portam demonstraram que entendem as bases sobre as quais todo código é construído.',
    'https://img.icons8.com/color/48/code.png',
    'EPIC'
);

-- Badge 2: Nível Explorador (Explorador de Sintaxe)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 2),
    'Mestre dos Arrays',
    'Domador de Estruturas de Dados',
    'Outorgado aos exploradores que conquistaram o poderoso Array Archon. Este badge simboliza maestria em manipulação de coleções de dados, ordenação, filtragem e transformações complexas. Apenas aqueles que pensam em termos de estruturas de dados merecem esta honra.',
    'https://img.icons8.com/color/48/matrix.png',
    'EPIC'
);

-- Badge 3: Nível Praticante (Praticante Dedicado)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 3),
    'Arquiteto de Objetos',
    'Mestre da Orientação a Objetos',
    'Conferido aos praticantes que derrotaram o sábio Object Oracle. Este badge representa profundo entendimento de POO: classes, herança, encapsulamento e polimorfismo. Portadores desta insígnia sabem modelar o mundo real em código elegante e reutilizável.',
    'https://img.icons8.com/color/48/class.png',
    'EPIC'
);

-- Badge 4: Nível Conhecedor (Conhecedor das Estruturas)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 4),
    'Domador de Exceções',
    'Guardião da Resiliência',
    'Concedido aos conhecedores que sobreviveram ao implacável Exception Executioner. Este badge certifica habilidade suprema em tratamento de erros, validação robusta e código resiliente. Aqueles que o carregam escrevem software que falha graciosamente e se recupera com elegância.',
    'https://img.icons8.com/color/48/shield.png',
    'EPIC'
);

-- Badge 5: Nível Habilidoso (Programador Habilidoso)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 5),
    'Mestre da Assincronicidade',
    'Senhor das Operações Paralelas',
    'Outorgado aos habilidosos que derrotaram o elusivo Async Assassin. Este badge simboliza domínio completo de programação assíncrona, paralelismo e concorrência. Portadores desta condecoração orquestram operações simultâneas com a precisão de um maestro.',
    'https://img.icons8.com/color/48/parallel-tasks.png',
    'EPIC'
);

-- Badge 6: Nível Experiente (Desenvolvedor Experiente)
INSERT INTO tb_badges (level_id, name, title, description, icon_url, rarity)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 6),
    'Caçador de Performance',
    'Otimizador Supremo',
    'Conferido aos experientes que exorcizaram o terrível Performance Phantom. Este badge representa maestria em otimização, algoritmos eficientes e arquitetura de alto desempenho. Aqueles que o possuem escrevem código que não apenas funciona, mas voa.',
    'https://img.icons8.com/color/48/rocket.png',
    'EPIC'
);

