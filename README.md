# Save Your Links

O Save Your Links é um projeto para salvar e consultar links favoritos em uma conta de usuário. A proposta é reunir os links de cada pessoa em um único lugar e oferecer um buscador para encontrá-los com facilidade.

## Tecnologias

- **Java 21:** linguagem utilizada no desenvolvimento da aplicação.
- **Spring Boot:** estrutura da aplicação web.
- **Thymeleaf:** criação das páginas e renderização de conteúdo no servidor.
- **Spring Data JPA:** persistência e acesso aos dados.
- **Azure SQL Database:** banco de dados na nuvem para armazenar as contas e os links dos usuários, com conexão pelo driver JDBC do SQL Server.
- **Maven:** gerenciamento de dependências e build do projeto.

## Planejamento de versões

As versões abaixo representam a evolução planejada do projeto.

### Versão 1.0.0 — Conta e cadastro de links

A primeira versão terá como foco permitir que cada usuário tenha uma conta e cadastre seus links favoritos diretamente no sistema. Cada link ficará associado à conta do usuário que o salvou.

### Versão 1.2.0 — Busca e sincronização com extensão

Esta versão ampliará o gerenciamento dos favoritos com:

- Um buscador para encontrar os links salvos na conta.
- Integração com uma extensão de navegador para salvar links durante a navegação.
- Sincronização entre a extensão e o sistema: ao salvar um link pela extensão, ele será armazenado na conta do usuário e ficará disponível no sistema.

### Versão 1.3.0 — Agente leitor de sites e chat

Ao salvar um link, um agente lerá o conteúdo do site e gerará uma descrição sobre ele. Essa descrição ajudará o usuário a identificar o assunto de cada página salva.

A versão também contará com um chat para que o usuário possa conversar sobre o conteúdo dos sites salvos e obter informações sobre eles.
