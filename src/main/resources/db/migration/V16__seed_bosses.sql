-- Migration V16: Seed bosses with epic data

-- Boss 1: Nível Iniciante (Aprendiz do Código)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 1),
    'Syntax Sentinel',
    'O Guardião da Sintaxe',
    'Um construto ancestral criado para proteger os fundamentos da programação. Seus olhos brilham com o conhecimento das estruturas básicas, e sua armadura é forjada com caracteres especiais. Ele testa aqueles que desejam progredir, garantindo que apenas os dignos compreendam verdadeiramente os alicerces do código.',
    'Crie uma aplicação console que demonstre domínio completo de variáveis, condicionais, loops e funções básicas. Implemente um mini-jogo (pedra-papel-tesoura, adivinhação de números, ou quiz interativo) que utilize todas essas estruturas de forma elegante e bem comentada.',
    150,
    'Vencedor da Sintaxe',
    'Derrotou o Syntax Sentinel e provou domínio dos fundamentos',
    'https://img.icons8.com/color/96/sentinel.png',
    'https://img.icons8.com/color/48/code.png'
);

-- Boss 2: Nível Explorador (Explorador de Sintaxe)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 2),
    'Array Archon',
    'O Senhor das Estruturas de Dados',
    'Uma entidade dimensional que manipula arrays e listas com maestria sobrenatural. Ele desafia aventureiros a dominarem a arte de organizar e manipular coleções de dados. Suas técnicas envolvem transformações complexas que separam iniciantes de verdadeiros exploradores.',
    'Desenvolva um sistema de gerenciamento (biblioteca, inventário ou playlist) usando arrays/listas. Implemente operações CRUD, busca, filtragem e ordenação. Adicione funcionalidades avançadas como estatísticas e relatórios. Código deve ser limpo e eficiente.',
    200,
    'Mestre dos Arrays',
    'Conquistou o Array Archon e dominou estruturas de dados fundamentais',
    'https://img.icons8.com/color/96/data-structure.png',
    'https://img.icons8.com/color/48/matrix.png'
);

-- Boss 3: Nível Praticante (Praticante Dedicado)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 3),
    'Object Oracle',
    'O Oráculo dos Objetos',
    'Um sábio milenar que enxerga o mundo através das lentes da Programação Orientada a Objetos. Ele testa a capacidade de seus desafiantes de modelar a realidade em classes, encapsular segredos em propriedades privadas e orquestrar comportamentos através de métodos. Apenas aqueles que pensam em objetos podem vencê-lo.',
    'Projete e implemente um sistema orientado a objetos (sistema de RPG, simulação de empresa, ou rede social simplificada). Use classes, herança, encapsulamento e polimorfismo. Mínimo de 5 classes bem relacionadas, com métodos significativos e boas práticas de OOP.',
    250,
    'Arquiteto de Objetos',
    'Derrotou o Object Oracle e dominou os pilares da POO',
    'https://img.icons8.com/color/96/object.png',
    'https://img.icons8.com/color/48/class.png'
);

-- Boss 4: Nível Conhecedor (Conhecedor das Estruturas)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 4),
    'Exception Executioner',
    'O Carrasco das Exceções',
    'Um vigilante impiedoso que pune código mal estruturado e despreparado para falhas. Ele ataca com erros inesperados, nulls traiçoeiros e edge cases devastadores. Apenas desenvolvedores que dominam o tratamento de exceções e validação robusta sobrevivem ao seu julgamento.',
    'Crie uma API REST ou aplicação web com tratamento de erros profissional. Implemente middleware de erros, validação de entrada, logging estruturado e respostas de erro consistentes. Adicione testes que validem cenários de erro (try-catch, custom exceptions, error boundaries).',
    300,
    'Domador de Exceções',
    'Sobreviveu ao Exception Executioner com código resiliente',
    'https://img.icons8.com/color/96/error.png',
    'https://img.icons8.com/color/48/shield.png'
);

-- Boss 5: Nível Habilidoso (Programador Habilidoso)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 5),
    'Async Assassin',
    'O Assassino Assíncrono',
    'Uma sombra que se move entre threads e processos, atacando quando menos se espera. Mestre do paralelismo e da concorrência, ele desafia desenvolvedores a orquestrarem operações assíncronas sem cair em race conditions ou deadlocks. Sua natureza imprevisível requer código perfeitamente sincronizado.',
    'Implemente uma aplicação que utilize programação assíncrona intensivamente: chame múltiplas APIs externas, processe dados em paralelo, implemente cache e retry logic. Use Promises/async-await (JS), Tasks/async (C#), ou Futures/async (Rust/Python). Demonstre tratamento correto de concorrência.',
    400,
    'Mestre da Assincronicidade',
    'Derrotou o Async Assassin e dominou a programação assíncrona',
    'https://img.icons8.com/color/96/async.png',
    'https://img.icons8.com/color/48/parallel-tasks.png'
);

-- Boss 6: Nível Experiente (Desenvolvedor Experiente)
INSERT INTO tb_bosses (level_id, name, title, description, challenge, xp_reward, badge_name, badge_description, image_url, badge_icon_url)
VALUES (
    (SELECT id FROM tb_levels WHERE order_level = 6),
    'Performance Phantom',
    'O Fantasma da Performance',
    'Uma entidade elusiva que pune código ineficiente e descuidado. Ele amplifica a latência, multiplica o uso de memória e expõe gargalos ocultos. Somente desenvolvedores que compreendem profundamente algoritmos, complexidade computacional e otimização podem exorcizá-lo. Seu desafio final exige velocidade e eficiência supremas.',
    'Otimize uma aplicação existente ou crie uma do zero focando em performance: implemente algoritmos eficientes (busca, ordenação), use estruturas de dados apropriadas, otimize queries de banco de dados, adicione caching estratégico e implemente paginação. Forneça benchmarks antes/depois provando melhorias de pelo menos 50%.',
    500,
    'Caçador de Performance',
    'Exorcizou o Performance Phantom com código otimizado e eficiente',
    'https://img.icons8.com/color/96/speed.png',
    'https://img.icons8.com/color/48/rocket.png'
);

