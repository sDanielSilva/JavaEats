### Análise do Código

Este código é para uma classe de controlo JavaFX chamada `dashboardCategoriaController`. É responsável por gerir categorias numa interface gráfica de utilizador (GUI) e interagir com uma base de dados. A classe inclui métodos para **ligação à base de dados**, **configuração de um `TableView` para mostrar categorias**, **carregamento de categorias da base de dados**, **adição, edição e remoção de categorias**, e **exibição de mensagens de erro**.

### Exemplo de Utilização

```java
// Criar uma instância da classe dashboardCategoriaController
dashboardCategoriaController controller = new dashboardCategoriaController();
```
```java
// Estabelecer ligação à base de dados
controller.conectarBaseDeDados();
```
```java
// Configurar o TableView para mostrar categorias
controller.configurarTableView();
```
```java
// Carregar categorias da base de dados e mostrá-las no TableView
controller.carregarCategorias();
```
```java
// Adicionar uma nova categoria
controller.adicionarCategoria();
```
```java
// Editar uma categoria selecionada
controller.editarCategoria();
```
```java
// Atualizar a categoria selecionada com um novo nome
controller.atualizarCategoria();
```
```java
// Apagar uma categoria selecionada
controller.apagarCategoria();
```

#### Funcionalidades Principais

- Ligar-se a uma base de dados usando a classe `ConectarBD`.
- Configurar um `TableView` para mostrar categorias.
- Carregar categorias da base de dados e mostrá-las no `TableView`.
- Adicionar uma nova categoria à base de dados e mostrá-la no `TableView`.
- Editar o nome de uma categoria selecionada na base de dados e atualizá-la no `TableView`.
- Remover uma categoria selecionada da base de dados e retirá-la do `TableView`.
- Exibir mensagens de erro em caso de falhas na ligação ou operações na base de dados.

#### Métodos

- `initialize()`: Inicializa a aplicação ligando-se à base de dados, configurando o `TableView` e carregando categorias.
- `conectarBaseDeDados()`: Estabelece ligação à base de dados usando a classe `ConectarBD` e inicializa o objeto `GerirBD` para operações na base de dados.
- `configurarTableView()`: Configura as colunas do `TableView` para mostrar o ID e o nome da categoria.
- `carregarCategorias()`: Carrega categorias da base de dados usando um `PreparedStatement` e um `ResultSet`, e adiciona-as ao `TableView`.
- `apagarCategoria(ActionEvent event)`: Apaga a categoria selecionada da base de dados usando a classe `GerirBD` e remove-a do `TableView`.
- `adicionarCategoria(ActionEvent actionEvent)`: Adiciona uma nova categoria à base de dados usando a classe `GerirBD` e adiciona-a ao `TableView`.
- `editarCategoria(ActionEvent event)`: Recupera a categoria selecionada do `TableView` e define o seu nome no campo de texto para edição.
- `atualizarCategoria(ActionEvent actionEvent)`: Atualiza o nome da categoria selecionada na base de dados usando a classe `GerirBD` e atualiza o `TableView`.
- `mostrarAlerta(String título, String cabeçalho)`: Mostra um diálogo de alerta de erro com um título e mensagem de cabeçalho especificados.
- `limparCampoTexto()`: Limpa o campo de texto usado para adicionar ou editar nomes de categoria.

#### Campos

- `tableViewCategorias`: O componente `TableView` para mostrar categorias.
- `idColumn`: A `TableColumn` para mostrar os IDs das categorias.
- `nomeColumn`: A `TableColumn` para mostrar os nomes das categorias.
- `nomeField`: O `TextField` para inserir novos nomes de categoria ou para edição.
- `conexao`: O objeto `Connection` para a ligação à base de dados.
- `gerirBD`: O objeto `GerirBD` para realizar operações na base de dados.
