-- Listar funcionarios
SELECT * FROM Funcionario;

-- Listar mesas
SELECT * FROM Mesa;

-- Listar itens dos pedidos da mesa
SELECT * FROM PedidoDetalhe;

-- Listar itens por categoria
SELECT * FROM Item WHERE id_categoria = ?; 

-- Listar pedidos por area de preparo
SELECT * FROM Pedido JOIN Item ON Pedido.id_pedido = Item.id_item WHERE Item.area_preparo = '?';

-- Emitir fatura
SELECT * FROM PagamentoAtendimento WHERE id_pagatendimento = ?;

-- Listar todas as faturas do dia
SELECT * FROM PagamentoAtendimento WHERE data_hora LIKE '% ? %';

-- Remover funcionario
DELETE FROM Funcionario WHERE id_funcionario = ?;

-- Remover categoria
DELETE FROM Categoria WHERE id_categoria = ?;

-- Remover item
DELETE FROM Item WHERE id_item = ?;

-- Remover mesa
DELETE FROM Mesa WHERE id_mesa = ?;

-- Remover cargo
DELETE FROM Cargo WHERE id_cargo = ?;

-- Atualizar funcionario
UPDATE Funcionario SET nome = '?', password = '?', id_cargo = ? WHERE id_funcionario = ?;

-- Atualizar categoria
UPDATE Categoria SET nome = '?' WHERE id_categoria = ?;

-- Atualizar item
UPDATE Item SET nome = '?', preco = ?, area_preparo = '?', id_categoria = ? WHERE id_item = ?;

-- Atualizar itens dos pedidos da mesa
UPDATE PedidoDetalhe SET quantidade = ?, data_hora = '?' WHERE id_pedido = ? AND id_item = ?;

-- Atualizar cargo
UPDATE Cargo SET descricao = '?' WHERE id_cargo = ?;

-- Adicionar funcionario
INSERT INTO Funcionario (nome, password, id_cargo) VALUES ('?', '?', ?);

-- Adicionar categoria
INSERT INTO Categoria (nome) VALUES ('?');

-- Adicionar item
INSERT INTO Item (nome, preco, area_preparo, id_categoria) VALUES ('?', ?, '?', ?);

-- Adicionar mesa
INSERT INTO Mesa (status) VALUES ('?');

-- Adicionar cargo
INSERT INTO Cargo (descricao) VALUES ('?');

-- Tipo de pagamento mais comum num intervalo de tempo:
SELECT tipo_pagamento, COUNT(*) AS NumeroDePagamentos
FROM PagamentoAtendimento
WHERE data_hora BETWEEN '2023-12-12%' AND '2023-12-17%'
GROUP BY tipo_pagamento
ORDER BY NumeroDePagamentos DESC;

-- Total faturado num intervalo de tempo:
SELECT SUM(preco_total) AS TotalFaturado
FROM PagamentoAtendimento
WHERE data_hora BETWEEN '2023-12-13%' AND '2023-12-17%';

-- Total faturado por mesa num intervalo de tempo:
SELECT id_mesa, SUM(preco_total) AS ReceitaDaMesa
FROM PagamentoAtendimento
WHERE data_hora BETWEEN '2023-12-13%' AND '2023-12-17%'
GROUP BY id_mesa
ORDER BY ReceitaDaMesa DESC
