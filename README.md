<a id="inicio"></a>
## Mi Concorrência e Conectividade - Problema 1

Este documento mostra os detalhes de implementação de um
sistema de consumo inteligente.

O projeto consiste na simulação de um medidor no qual esse envia constantemente valores aleatórios
para um servidor via socket UDP e também a implementação de uma API REST com socket TCP para que o 
cliente possa fazer requisições solicitando, informações de consumo e fatura.

O sistema possui as seguinte funcionalidades:

**API REST**

- Consulta de consumo de energia total;
- Consulta de consumos de energia em determinados horários
- Geração de fatura dos clientes
- Consulta de alertas de consumo excessivo

**Medidor Inteligente**
- Configurar parâmetros para envio de medição(Host, Porta, Intervalo de medição e código do contador)
- Definir taxa de consumo de energia

### Seções 

&nbsp;&nbsp;&nbsp;[**1.** Diagrama do projeto](#secao1)

&nbsp;&nbsp;&nbsp;[**2.** Rotas utilizadas](#secao2)

## Conceitos

**HTTP** - Protocolo de rede da camada de aplicação que permite a obtenção de recursos. É a base de qualquer troca de dados na Web e um protocolo cliente-servidor, o que significa que as requisições são iniciadas pelo destinatário, geralmente um navegador da Web. O HTTP é enviado sobre um protocolo da camada de transporte, o TCP.

**TCP** -

**UDP** -

**Sockets** -


## Diagrama do projeto

![Diagrama pbl1png](https://user-images.githubusercontent.com/72475500/227080946-8667a983-8493-41df-b975-191ec4e137c2.png)


O diagrama acima mostra o fluxo das mensagens trocadas entre clientes e servidor. No sistema foi utilizado os sockets para fazer a comunicação entre cada uma
das entidades do sistema(cliente, servidor e medidor).

#### Comunicação entre consumidor e API REST

Na comunicação entre o cliente(consumidor) e o servidor foi utilizado um socket TCP para criar um servidor que entendesse mensagens HTTP. Como mostrado na figura, o cliente envia uma mensagem para o servidor, ao chegar, será verificada se a estrutura desta é válida, caso seja, a mesma é convertida para um objeto que representa uma requisição HTTP, onde este será enviado para a API REST, será processado e poderá fazer buscas por informações na base de dados.

#### Comunicação entre dispositivo medidor e servidor

Para a comunicação entre o medidor e o servidor também foi utilizado um socket, entretanto, utilizando um protocolo diferente, o UDP. O medidor ficará enviando dados para o servidor que os salvará na base de dados. Como o medidor inteligente estará enviando mensagens ao servidor de forma síncrona(em um determinado intervalo de tempo) utilizar um protocolo como o TCP junto com HTTP seria muito custoso já que uma mensagem de resposta seria retornada para o dispositivo e cada requisição passaria por um processo de verificação para checar se sua estrutura está correta o que não é necessário nessa transmissão, pois um simples protocolo que especifica que no corpo mensagem deverá conter o código do medidor, horário e valor da medição já é o suficiente.

## Servidor HTTP

Visto que o usuário utilizará o sistema através de uma API REST, é necessário fazer o uso de um servidor HTTP já que uma API desse tipo tem sua comunicação feita por este. O meio de comunicação utilizado no projeto é um socket que puro não é capaz de entender este protocolo, sendo assim, foi necessário fazer com o socket fosse capaz de entender essas mensagens e que também o mesmo pudesse enviar mensagens seguindo esse padrão.

### Criando uma representação de uma requisição HTTP

Para facilitar a utilização do protocolo HTTP foram feitas representações com classes das partes que o compõe, como por exemplo: requisições e respostas. 

Para fazer esse mapeamento foi necessário primeiro entender como funciona cada uma dessas.

### Requisições HTTP

Uma Requisição HTTP possui a seguinte estrutura:

```
método /caminho?paramêtros versão_do_http CRLF
cabeçalhos
linha_vazia
corpo_da_mensagem
```
Na primeira linha devemos fornecer o método da requisição, por exemplo `GET` ou `POST`, seguido da rota que pode conter parâmetros e a versão do protocolo HTTP, no final da linha deve ser incluido um CRLF(\r\n) para que o cursor seja posicionado no inicio da linha e quebre para a próxima.

A segunda parte dessa estrutura contém os cabeçalhos da requisição, esses cabeçalhos irão definir informações extras sobre a solicitação como tamanho e tipo de um arquivo. Cada cabeçalho deve conter também no final de sua linha o CRLF(\r\n).

A terceira parte contém apenas uma linha vazia que separa o cabeçalho do corpo da mensagem.

Por fim, tem-se o corpo da mensagem onde usuário irá passar informações para o servidor. Um belo exemplo seria uma requisição de login onde o usuário estaria passando seu nome e senha, por exemplo.

Abaixo um exemplo simples de requisição HTTP:
```
GET /login HTTP/1.1
Content-Type: application/json

{
"user":"user name",
"password":"123"
}
```

### Respostas HTTP

Uma Resposta HTTP possui a seguinte estrutura:

```
versão_do_http código_de_status mensagem_de_status CRLF
cabeçalhos
linha_vazia
corpo_da_mensagem
```
Na primeira linha devemos fornecer a versão do protocolo HTTP, o código de status da mensagem, como por exemplo, `200` e uma mensagem referente ao status que para o código mencionado seria `OK`. No final da linha também deve ser incluido um CRLF(\r\n) para que o cursor seja posicionado no inicio da linha e quebre para a próxima.

Assim como nas requisições, a segunda parte dessa estrutura contém os cabeçalhos, que irão especificar informações adicionais sobre a solicitação. Após cada cabeçalho um CRLF(\r\n) deve ser incluido.

A terceira parte contém uma linha vazia que separa o cabeçalho do corpo da mensagem, como nas requisições.

Por último vem o corpo da resposta, mostrando o resultado da solicitação.

Abaixo um exemplo simples de resposta HTTP:
```
HTTP/1.1 200 OK 
Content-Type: application/json

{
"mensagem":"Olá, mundo!"
}
```

### Traduzindo as mensagens recebidas

Após a etapa anterior, era necessário fazer o socket entender essa estrutura. Como as mensagens recebidas são texto, então a forma mais eficiente de traduzir essa mensagem foi através de manipulação de strings, assim foi possível separar cada parte da mensagem(primeira linha,cabeçalho e corpo) e trabalhar individualmente com cada uma. Com a requisição em pedaços, a primeira coisa a se fazer antes de enviá-la para a API foi validar se a estrutura é correta, montando uma representação de uma requisição HTTP para a API REST caso as validações sejam satisfeitas.

## API REST

Uma API  (Application Programming Interface ou Interface de Programação de Aplicação) é um conjunto de definições e protocolos usado no desenvolvimento e na integração de aplicações. 

REST(Representational State Transfer ou Transferência de Estado Representacional) não é um protocolo ou padrão, mas sim um conjunto de restrições de arquitetura, ou seja, uma API para ser considerada REST segue essas restrições. Os seguintes critérios são especificados:

- A arquitetura deve ser cliente/servidor(com solicitações gerenciadas por HTTP).
- Estabelecer uma comunicação stateless(nenhuma informação do cliente é armazenada entre solicitações GET e toda as solicitações são separadas e desconectadas) entre cliente e servidor.
- Armazenar dados em cache(para otimizar as interações entre cliente e servidor).
- Ter uma interface uniforme entre os componentes para que as informações sejam transferidas em um formato padronizado.
- Ter um sistema em camadas que organiza os tipos de servidores.
- Possibilitar código sob demanda (opcional).


Para a comunicação do consumidor com o sistema, foi utilizado de uma API REST onde o usuário pode fazer consultas relacionadas ao seu consumo e também
gerar faturas. Para acesso a essas funcionalidades a aplicação dispões de rotas específicas para cada recurso.



### Rotas utilizadas

|      **Rota**      	| **Métodos aceitos** 	|              **Parâmetros de consulta**             	| **Corpo da mensagem** 	|                                           **Descrição**                                          	|
|:------------------:	|:-------------------:	|:---------------------------------------------------:	|:---------------------:	|:------------------------------------------------------------------------------------------------:	|
|     */cliente*     	|    `POST`, `GET`    	|           `GET` - id(obrigatório)           	|   `POST`- nome  	|                        Rota utilizada para consultar e cadastrar clientes                        	|
| */consumo_energia* 	|        `GET`        	| id(obrigatório), offset(opcional) e limit(opcional) 	|           -           	|         Rota utilizada para consultar consumo de energia total e em determinados horários        	|
|      */fatura*     	|        `GET`        	|                   id(obrigatório)                   	|           -           	|                           Rota utilizada para gerar fatura de pagamento                          	|
|  */alerta_consumo* 	|        `GET`        	|                   id(obrigatório)                   	|           -           	| Rota utilizada para consultar alertas de consumo excessivo e grande variação na conta do usuário 	|

A API possui no total 4 rotas para acesso as informações do cliente, cada rota aceita apenas o método GET em requisições, com exceção da rota */cliente* que aceita também o método `POST`.

Se por acaso uma rota inválida for passada, o servidor irá retornar uma resposta de erro com status `400 Method Bad Request`. Caso o servidor não implemente o método passado na requisição, será retornada uma resposta de erro com status `501 Not Implemented` e caso um método seja reconhecido pelo servidor, mas a rota passada não o aceita, será retornada então uma mensagem de erro com status `405 Method Not Allowed.


As rotas também aceitam parâmetros de consulta para especificar informações necessárias para que a requisição ocorra com sucesso. Obrigatoriamente todas as rotas devem utilizar o parâmetro **id**, que especifica o id do cliente a ser buscado, com exceção do método `POST` da rota */cliente* que não recebe parâmetros de consulta e sim um corpo que contém o nome do usuário a ser cadastrado.

Além dessas, existe também dois parâmetros opcionais que são usados em conjunto para fazer a paginação dos consumos do usuário, sendo estes o **offset** e **limit**. O offset determina o id do primeiro consumo, o **limit** irá definir a quantidade de informações(consumos) que a pagina conterá. Por padrão a rota já implementa essa paginação, pegando os 6 últimos valores da lista de consumos e caso esses parâmetros sejam utilizados incorretamentes, essa página padrão também será utilizada.


#### Exemplos de uso das rotas

*GET /cliente*

```
http://localhost:8080/cliente?id=0
```


*POST /cliente*

```
http://localhost:8080/cliente

{"nome":"usuario"}
```

*GET /alerta_consumo*

```
http://localhost:8080/alerta_consumo?id=0
```

*GET /consumo_energia*

```
http://localhost:8080/consumo_energia?id=0
```

*GET /consumo_energia* com parâmetros de consulta

```
http://localhost:8080/consumo_energia?id=0&offset=5&limit=15
```

*GET /fatura*

```
http://localhost:8080/fatura?id=0
```

### Padrão MVC(Model, View, Controller)



#### ⬆️ [Voltar ao topo](#inicio)

