### Análise do Código

Este código é para uma classe de controlo JavaFX chamada `DashboardMesaController`. É responsável por por carregar e exibir as mesas e botões que representam as mesas do restaurante. **Também trata da lógica para ligação à base de dados, recuperação dos dados das mesas e abertura de uma nova janela para cada mesa**.

### Exemplo de Utilização

```java
DashboardMesaController controller = new DashboardMesaController();
```
```java
controller.initialize();
```

#### Funcionalidades Principais

- Estabelece ligação à base de dados e recupera os dados das mesas.
- Cria botões e rótulos para cada mesa.
- Calcula o valor total de cada mesa.
- Trata de eventos de clique nos botões para abrir uma nova janela para cada mesa.

#### Métodos

- `initialize()`: Estabelece ligação à base de dados, recupera os dados das mesas e chama o método `carregarMesasDoBanco()` para criar botões e rótulos para cada mesa.
- `carregarMesasDoBanco(Connection conexao)`: Recupera os dados das mesas da base de dados, calcula o valor total de cada mesa e cria botões e rótulos para cada mesa.
- `abrirJanelaMesa(int mesaId)`: Abre uma nova janela para a mesa selecionada, utilizando o ficheiro `Pedido.fxml` e passando o ID da mesa para o DashboardPedidoController.
- `fazerReserva(ActionEvent actionEvent)`: Abre uma nova janela para fazer uma reserva, utilizando o ficheiro `reservas.fxml`.
#### Campos

- `mesaContainer`: Um contentor VBox para guardar os botões e rótulos que representam as mesas.
- `stage`: O palco para a janela de reserva.