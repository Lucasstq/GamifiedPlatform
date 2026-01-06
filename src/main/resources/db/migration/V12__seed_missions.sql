-- Migration V12: Seed missions for all levels
-- Nível 1: Camponês - Fundamentos e Lógica
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(1, 'Primeiro Passo: Hello World', 'Crie seu primeiro programa Java que exibe "Hello, World!" no console. Entenda a estrutura básica de uma classe Java.', 50, 1),
(1, 'Variáveis e Tipos Primitivos', 'Crie um programa que declara e utiliza todos os tipos primitivos do Java (int, double, boolean, char, etc.).', 75, 2),
(1, 'Operadores Aritméticos', 'Desenvolva uma calculadora simples que realiza as 4 operações básicas (+, -, *, /).', 75, 3),
(1, 'Estruturas Condicionais', 'Crie um programa que verifica se um número é par ou ímpar, positivo ou negativo.', 100, 4),
(1, 'Laços de Repetição - For', 'Implemente um programa que exibe a tabuada de um número escolhido usando for.', 100, 5),
(1, 'Laços de Repetição - While', 'Crie um jogo de adivinhação onde o usuário tenta adivinhar um número entre 1 e 100.', 125, 6),
(1, 'Arrays Básicos', 'Desenvolva um programa que armazena 10 números em um array e calcula a média.', 150, 7),
(1, 'Métodos e Funções', 'Refatore seus programas anteriores criando métodos reutilizáveis.', 150, 8);

-- Nível 2: Aprendiz - Estruturas de Dados
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(2, 'ArrayList Dinâmico', 'Crie um sistema de cadastro de alunos usando ArrayList. Implemente adicionar, remover e listar.', 200, 1),
(2, 'LinkedList e Performance', 'Compare a performance de ArrayList vs LinkedList em diferentes cenários de inserção e remoção.', 250, 2),
(2, 'HashMap e Dicionários', 'Implemente um dicionário de termos técnicos usando HashMap com busca, inserção e remoção.', 250, 3),
(2, 'Set e Elementos Únicos', 'Crie um sistema que remove duplicatas de uma lista usando HashSet.', 200, 4),
(2, 'Stack - Pilha', 'Implemente um verificador de parênteses balanceados usando Stack.', 300, 5),
(2, 'Queue - Fila', 'Desenvolva um simulador de fila de atendimento bancário usando Queue.', 300, 6),
(2, 'Algoritmos de Ordenação', 'Implemente Bubble Sort e Selection Sort, compare a performance.', 350, 7),
(2, 'Busca Binária', 'Crie uma implementação de busca binária e compare com busca linear.', 350, 8);

-- Nível 3: Acolito - Java Orientado a Objetos
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(3, 'Classes e Objetos', 'Crie classes para modelar um sistema de biblioteca (Livro, Autor, Biblioteca).', 300, 1),
(3, 'Encapsulamento', 'Refatore suas classes aplicando encapsulamento com getters, setters e validações.', 350, 2),
(3, 'Herança', 'Implemente hierarquia de classes: Animal → Mamífero → Cachorro/Gato.', 400, 3),
(3, 'Polimorfismo', 'Crie sistema de pagamento com diferentes métodos (CartaoCredito, Pix, Boleto).', 450, 4),
(3, 'Interfaces', 'Implemente interfaces Comparable e Comparator para ordenar objetos customizados.', 400, 5),
(3, 'Classes Abstratas', 'Crie sistema de formas geométricas com classe abstrata Forma.', 400, 6),
(3, 'Exceções Customizadas', 'Desenvolva tratamento de erros com exceções personalizadas para seu sistema.', 450, 7),
(3, 'Generics', 'Implemente uma classe genérica Repositorio<T> que pode armazenar qualquer tipo.', 500, 8);

-- Nível 4: Forja - CRUD + APIs + Banco de Dados
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(4, 'Configuração Spring Boot', 'Configure um projeto Spring Boot com todas as dependências necessárias.', 400, 1),
(4, 'Modelagem de Entidades', 'Crie entidades JPA para um sistema de gerenciamento de produtos.', 500, 2),
(4, 'Repository Layer', 'Implemente repositories usando Spring Data JPA com queries customizadas.', 500, 3),
(4, 'Service Layer', 'Desenvolva a camada de serviços com regras de negócio e validações.', 600, 4),
(4, 'REST Controller - GET', 'Crie endpoints REST para listar e buscar produtos.', 500, 5),
(4, 'REST Controller - POST/PUT', 'Implemente endpoints para criar e atualizar produtos com validações.', 600, 6),
(4, 'REST Controller - DELETE', 'Adicione endpoint de exclusão com soft delete.', 500, 7),
(4, 'Tratamento de Exceções Global', 'Implemente exception handler global para padronizar respostas de erro.', 700, 8),
(4, 'DTOs e Mappers', 'Refatore usando DTOs e mappers para separar entidades de responses.', 700, 9),
(4, 'Paginação e Ordenação', 'Adicione suporte a paginação e ordenação nos endpoints de listagem.', 600, 10);

-- Nível 5: Cavaleiro Real - Engenharia de Software
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(5, 'Testes Unitários - JUnit', 'Escreva testes unitários para a camada de serviços com cobertura mínima de 80%.', 800, 1),
(5, 'Testes de Integração', 'Implemente testes de integração para seus controllers usando MockMvc.', 900, 2),
(5, 'Documentação com Swagger', 'Configure Swagger/OpenAPI e documente todos os endpoints da API.', 700, 3),
(5, 'Autenticação JWT', 'Implemente autenticação stateless com JWT tokens.', 1000, 4),
(5, 'Autorização com Roles', 'Adicione controle de acesso baseado em roles (ADMIN, USER).', 900, 5),
(5, 'Logs Estruturados', 'Configure logs estruturados com níveis adequados (INFO, WARN, ERROR).', 600, 6),
(5, 'Validações com Bean Validation', 'Implemente validações robustas usando Jakarta Validation.', 700, 7),
(5, 'Cache com Redis', 'Configure cache com Redis para otimizar consultas frequentes.', 1000, 8),
(5, 'Docker e Docker Compose', 'Containerize sua aplicação e banco de dados com Docker Compose.', 900, 9),
(5, 'CI/CD Pipeline', 'Configure pipeline de CI/CD com GitHub Actions ou GitLab CI.', 1100, 10);

-- Nível 6: Arcanista Supremo - Arquitetura Avançada
INSERT INTO tb_missions (level_id, title, description, xp_reward, order_number) VALUES
(6, 'Arquitetura Hexagonal', 'Refatore aplicação usando arquitetura hexagonal (ports and adapters).', 1500, 1),
(6, 'Microsserviços', 'Divida aplicação monolítica em microsserviços independentes.', 2000, 2),
(6, 'API Gateway', 'Implemente API Gateway para roteamento e load balancing.', 1500, 3),
(6, 'Event-Driven Architecture', 'Implemente comunicação assíncrona entre serviços usando mensageria (RabbitMQ/Kafka).', 2000, 4),
(6, 'CQRS Pattern', 'Separe comandos de queries usando pattern CQRS.', 1800, 5),
(6, 'Event Sourcing', 'Implemente Event Sourcing para auditoria completa de mudanças.', 2000, 6),
(6, 'Observabilidade', 'Configure stack de observabilidade (Prometheus, Grafana, Loki).', 1500, 7),
(6, 'Kubernetes Deployment', 'Deploy da aplicação em cluster Kubernetes com auto-scaling.', 2500, 8),
(6, 'Resiliência e Circuit Breaker', 'Implemente patterns de resiliência (Circuit Breaker, Retry, Timeout).', 1800, 9),
(6, 'Projeto Final Completo', 'Desenvolva projeto completo aplicando todos os conceitos aprendidos.', 3000, 10);

