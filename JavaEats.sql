CREATE TABLE `Cargo` (
  `id_cargo` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id_cargo`)
);

CREATE TABLE `Categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
);

CREATE TABLE `Funcionario` (
  `id_funcionario` int(10) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `id_cargofunc` int(10) DEFAULT NULL,
  `id_pedidofunc` int(10) DEFAULT NULL,
  `id_cargo` int(10) DEFAULT NULL,
  `id_pedido` int(10) DEFAULT NULL,
  PRIMARY KEY (`id_funcionario`),
  KEY `id_cargo_idx` (`id_cargo`),
  KEY `id_pedido_idx` (`id_pedido`),
  KEY `id_cargofunc_idx` (`id_pedidofunc`),
  CONSTRAINT `id_cargofunc` FOREIGN KEY (`id_pedidofunc`) REFERENCES `Cargo` (`id_cargo`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `id_pedidofunc` FOREIGN KEY (`id_pedidofunc`) REFERENCES `Funcionario` (`id_funcionario`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `Item` (
  `id_item` int(10) NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) DEFAULT NULL,
  `preco` int(10) DEFAULT NULL,
  `area_preparo` varchar(45) DEFAULT NULL,
  `id_categoria` int(10) DEFAULT NULL,
  PRIMARY KEY (`id_item`),
  KEY `id_categoria_idx` (`id_categoria`),
  CONSTRAINT `id_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `Categoria` (`id_categoria`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `Mesa` (
  `id_mesa` int(10) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id_mesa`)
);

CREATE TABLE `PagamentoAtendimento` (
  `id_pagatendimento` int(10) NOT NULL AUTO_INCREMENT,
  `data_hora` varchar(100) NOT NULL,
  `preco_total` double DEFAULT NULL,
  `tipo_pagamento` varchar(45) DEFAULT NULL,
  `id_mesa` int(10) DEFAULT NULL,
  PRIMARY KEY (`id_pagatendimento`),
  KEY `id_mesa_idx` (`id_mesa`),
  CONSTRAINT `id_mesa` FOREIGN KEY (`id_mesa`) REFERENCES `Mesa` (`id_mesa`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `Pedido` (
  `id_pedido` int(10) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(250) DEFAULT NULL,
  `id_pagamento` int(10) DEFAULT NULL,
  PRIMARY KEY (`id_pedido`),
  KEY `id_pagamento_idx` (`id_pagamento`),
  CONSTRAINT `id_pagamento` FOREIGN KEY (`id_pagamento`) REFERENCES `PagamentoAtendimento` (`id_pagatendimento`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `PedidoDetalhe` (
  `id_pedido` int(10) NOT NULL,
  `id_item` int(10) NOT NULL,
  `quantidade` int(10) DEFAULT NULL,
  `data_hora` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_pedido`,`id_item`),
  KEY `id_item_idx` (`id_item`),
  CONSTRAINT `id_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `Pedido` (`id_pedido`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `id_item` FOREIGN KEY (`id_item`) REFERENCES `Item` (`id_item`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

--

INSERT INTO Cargo (descricao) VALUES
('Gerente'),
('Atendente'),
('Cozinheiro');

INSERT INTO Categoria (nome) VALUES
('Bebidas'),
('Pratos Principais'),
('Sobremesas'),
('Entradas');

INSERT INTO Funcionario (id_funcionario, nome, password, id_cargofunc, id_pedidofunc, id_cargo, id_pedido) VALUES
(1, 'Lucas', '321', NULL, NULL, 1, NULL),
(2, 'Dani', '123', NULL, NULL, 2, NULL),
(3, 'Cravo', '321', NULL, NULL, 3, NULL),
(4, 'Bruno', '123', NULL, NULL, 2, NULL),
(5, 'Pirre', '321', NULL, NULL, 1, NULL);

INSERT INTO Item (nome, preco, area_preparo, id_categoria) VALUES
('Refrigerante', 5, 'Bar', 1),
('Feijoada', 20, 'Cozinha', 2),
('Pudim', 8, 'Cozinha', 3),
('Lasanha', 10, 'Cozinha', 4),
('Leitão', 6, 'Cozinha', 4);

INSERT INTO Mesa (status) VALUES
('Livre'),
('Ocupada'),
('Reservada'),
('Em limpeza');

INSERT INTO PagamentoAtendimento (data_hora, preco_total, tipo_pagamento, id_mesa) VALUES
('2023-12-13 15:00:00', 50, 'Cartão', 2),
('2023-12-13 16:30:00', 30, 'Dinheiro', 1),
('2023-12-13 17:45:00', 15, 'Dinheiro', 3),
('2023-12-13 18:20:00', 40, 'Cartão', 4);

INSERT INTO Pedido (descricao, id_pagamento) VALUES
('Pedido 1', 1),
('Pedido 2', 2),
('Pedido 3', 3),
('Pedido 4', 4);

INSERT INTO PedidoDetalhe (id_pedido, id_item, quantidade, data_hora) VALUES
(1, 1, 2, '2023-12-13 15:10:00'),
(1, 2, 1, '2023-12-13 15:15:00'),
(2, 3, 3, '2023-12-13 16:45:00'),
(3, 4, 2, '2023-12-13 18:00:00');