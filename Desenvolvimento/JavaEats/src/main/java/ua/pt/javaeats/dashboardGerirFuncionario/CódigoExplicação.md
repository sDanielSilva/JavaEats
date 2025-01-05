### Análise do Código

Este código é para uma classe de controlo JavaFX chamada `dashboardFuncionarioController`. É responsável por gerir funcionários numa interface gráfica de utilizador (GUI) e interagir com uma base de dados. A classe inclui métodos para **ligação à base de dados**, **configuração de um `TableView` para mostrar funcionários**, **carregamento de funcionários da base de dados**, **adição, edição e remoção de funcionários**, e **exibição de mensagens de erro**.

### Exemplo de Utilização

```java
// Criar uma instância da classe dashboardFuncionarioController
dashboardFuncionarioController controller = new dashboardFuncionarioController();
```
```java
// Inicializar a ligação à base de dados e carregar dados dos funcionários
controller.initialize();
```
```java
// Criar um novo funcionário e guardá-lo na base de dados
controller.criarFuncionario();
```
```java
// Editar um funcionário existente e atualizar a base de dados
controller.editarFuncionario();
```
```java
// Remover um funcionário da base de dados
controller.removerFuncionario();
```
```java
// Atualizar os detalhes de um funcionário e guardar as alterações na base de dados
controller.atualizarFuncionario();
```

#### Funcionalidades Principais

- Estabelece uma ligação a uma base de dados e inicializa um gestor para operações na base de dados.
- Carrega dados dos funcionários da base de dados e exibe-os numa TableView.
- Permite criar, editar e remover funcionários, refletindo as alterações na base de dados.

#### Métodos

- `initialize()`: Inicializa a ligação à base de dados e carrega dados dos funcionários para a `TableView`.
- `conectarBaseDeDados()`: Estabelece ligação à base de dados usando a classe `ConectarBD` e inicializa o objeto `inicializarConexao()` Estabelece uma ligação à base de dados e inicializa o gestor da base de dados.
- `inicializarGerirBD()`: Inicializa o gestor da base de dados com a ligação estabelecida.
- `carregarFuncionarios()`: Carrega dados dos funcionários da base de dados e adiciona-os à `TableView'`.
- `criarFuncionario()`: Cria um novo funcionário com os dados fornecidos, guarda-o na base de dados e adiciona-o à `TableView`.
- `editarFuncionario()`: Permite editar um funcionário existente ao selecioná-lo na `TableView`.
- `removerFuncionario()`: Remove o funcionário selecionado da base de dados e da `TableView`.
- `atualizarFuncionario()`: Atualiza os detalhes do funcionário selecionado com os novos valores introduzidos nos campos de texto, guarda as alterações na base de dados e atualiza a `TableView`.
- `preencherCampos()`: Preenche os campos de texto com os detalhes do funcionário selecionado.
- `limparCamposTexto()`: Limpa o campo de textos.
- `mostrarAlerta()`: Mostra um alerta de erro com o título e mensagem especificados.

#### Campos

- `tableViewFuncionarios`: TableView para exibir os dados dos funcionários.
- `nomeField, passwordField, idField, idCargoField`: Campos de texto para introduzir detalhes do funcionário.
- `conexao`: Objeto de conexão para ligação à base de dados.
- `gerenciadorBD`: Instância da classe GerirBD para gerir operações na base de dados.
- `funcionarioParaEditar`: Objeto de funcionário para armazenar o funcionário em edição.