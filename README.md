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

### Instalação do projeto

#### Executando o arquivo jar do servidor

`java -jar pbl1_redes-1.0-SNAPSHOT-jar-with-dependencies.jar`

#### Executando o arquivo jar do medidor

`java -jar pbl1_redes_medidor-1.0-jar-with-dependencies.jar` 

#### Executando através do docker compose

`docker compose up`

### Seções 

&nbsp;&nbsp;&nbsp;[**1.** Diagrama do projeto](#secao1)

&nbsp;&nbsp;&nbsp;[**2.** Servidor HTTP](#secao2)

&nbsp;&nbsp;&nbsp;[**3.** API REST](#secao3)

&nbsp;&nbsp;&nbsp;[**4.** Medidor Inteligente](#secao4)

&nbsp;&nbsp;&nbsp;[**5.** Materiais Utilizados](#secao5)

<a id="secao1"></a>
## Diagrama do projeto

![Diagrama pbl1png](https://user-images.githubusercontent.com/72475500/227080946-8667a983-8493-41df-b975-191ec4e137c2.png)


O diagrama acima mostra o fluxo das mensagens trocadas entre clientes e servidor. No sistema foi utilizado os sockets para fazer a comunicação entre cada uma
das entidades do sistema(cliente, servidor e medidor).

#### Comunicação entre consumidor e API REST

Na comunicação entre o cliente(consumidor) e o servidor foi utilizado um socket TCP para criar um servidor que entendesse mensagens HTTP. Como mostrado na figura, o cliente envia uma mensagem para o servidor, ao chegar, será verificada se a estrutura desta é válida, caso seja, a mesma é convertida para um objeto que representa uma requisição HTTP, onde este será enviado para a API REST, será processado e poderá fazer buscas por informações na base de dados.

#### Comunicação entre dispositivo medidor e servidor

Para a comunicação entre o medidor e o servidor também foi utilizado um socket, entretanto, utilizando um protocolo diferente, o UDP. O medidor ficará enviando dados para o servidor que os salvará na base de dados. Como o medidor inteligente estará enviando mensagens ao servidor de forma síncrona(em um determinado intervalo de tempo) utilizar um protocolo como o TCP junto com HTTP seria muito custoso já que uma mensagem de resposta seria retornada para o dispositivo e cada requisição passaria por um processo de verificação para checar se sua estrutura está correta o que não é necessário nessa transmissão, pois um simples protocolo que especifica que no corpo mensagem deverá conter o código do medidor, horário e valor da medição já é o suficiente.

<a id="secao2"></a>
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

### Tradução das mensagens recebidas

Após a etapa anterior, era necessário fazer o socket entender essa estrutura. Como as mensagens recebidas são texto, então a forma mais eficiente de traduzir essa mensagem foi através de manipulação de strings, assim foi possível separar cada parte da mensagem(primeira linha,cabeçalho e corpo) e trabalhar individualmente com cada uma. Com a requisição em pedaços, a primeira coisa a se fazer antes de enviá-la para a API foi validar se a estrutura é correta, montando uma representação de uma requisição HTTP para a API REST caso as validações sejam satisfeitas.

### Múltiplos clientes no servidor

Após fazer o socket entender mensagens HTTP, foi necessário fazer com que ele pudesse receber requisições de múltiplos clientes ao mesmo tempo, para isso foi necessário fazer a utilizando de threads.

O funcionamento da conexão do servidor com o cliente acontece da seguinte forma: O servidor fica esperando um cliente se conectar e após a conexão ser feita o servidor irá processar a sua requisição, entretanto, o servidor só processa a requisição do próximo usuário após terminar a do atual. A forma de resolver esse problema é após aceitar a conexão com o cliente o servidor criar uma nova thread para o usuário, essa thread será a responsável por gerenciar a solicitação. Sendo assim, agora cada usuário terá sua própria thread o que permite o servidor receber vários clientes ao mesmo tempo.

<a id="secao3"></a>
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

### Padrão MVC(Model, View, Controller)

A API REST foi dividida em camadas utilizando o Padrão MVC(Model, View, Controller) nesse padrão o projeto é dividido em três camadas, cada uma responsável por um aspecto da aplicação. A view é a camada de visualização onde são retornadas ao usuário as informações que ele solicitou, a camada de controle esta toda a regra de negócio de uma view, são os controllers que conversam com a próxima camada, a camada de modelo(Model), nessa camada fica todas as entidades do sistema. Para deixar o código mais coeso, foi também implementada uma camada de repositórios, essa camada é a responsável por se comunicar com a base de dados, fazendo leitura ou escrita.

Cada rota da aplicação possui uma view, que possui um controller e esta se comunica com os repositórios e modelos necessários.

![Diagrama API - pbl1 redes drawio](https://user-images.githubusercontent.com/72475500/227214755-a08e77b9-0a80-4cae-8d40-7071542f6c9d.png)

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

Resposta:

```
{
  "id":0,
  "cliente":"nome do usuario",
  "codigo_medidor":00000
}
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

Resposta:

```
{
   "cliente":"sergivaldo",
   "consumo":{
      "messagem":"estado do consumo(normal, excessivo)",
      "consumo_medio":"0.00",
      "limite_consumo_normal":"00.00"
   },
   "conta":{
      "messagem":"estado das contas(normal, grande variação)",
      "preco_medio":"0.00",
      "limite_preco_normal":"00.00"
   }
}
```

*GET /consumo_energia*

```
http://localhost:8080/consumo_energia?id=0
```

Resposta:

```
{
   "id":0,
   "cliente":"nome do usuario",
   "consumo_total":"0.00",
   "consumos":[
      {
         "consumo":0.00,
         "horario_medicao":"dd/MM/yyyy HH:mm:ss"
      }
   ]
}
```

*GET /consumo_energia* com parâmetros de consulta

```
http://localhost:8080/consumo_energia?id=0&offset=0&limit=3
```

Resposta:

```
{
   "id":0,
   "cliente":"nome do usuario",
   "consumo_total":"0.00",
   "consumos":[
      {
         "consumo":0.00,
         "horario_medicao":"dd/MM/yyyy HH:mm:ss"
      },
      {
         "consumo":0.00,
         "horario_medicao":"dd/MM/yyyy HH:mm:ss"
      },
      {
         "consumo":0.00,
         "horario_medicao":"dd/MM/yyyy HH:mm:ss"
      }
   ]
}
```
*GET /fatura*

```
http://localhost:8080/fatura?id=0
```

Resposta:
```
{
  "nome":"nome do usuário",
  "codigo_medidor":"0000000",
  "preco":"00.00"
}
```
<a id="secao4"></a>
## Medidor Inteligente

O medidor dentro do sistema será o dispositivo que enviará as medições de consumo do seu cliente. Esse consumo é gerado de forma aleatória com base em uma taxa de 
consumo que pode ser especificada pelo usuário no menu, o cálculo para gerar o valor é feito da seguinte forma: *`aleatório entre 0 e 1.0 * taxa definida`*

O dispositivo também possui uma interface para fazer configurações de conexão com o servidor, nesta configuração pode ser especificado o código do medidor, host do servidor, porta do servidor  e o intervalo de tempo em segundo no qual será enviado os dados. Na primeira vez que o medidor é iniciado esse menu é mostrado já que o aparelho ainda não foi configurado, sendo assim, somente após a sua configuração o medidor poderá enviar informações ao servidor.

### Protocolo de comunicação

O medidor possui um protocolo simples para fazer o envio dos dados de consumo, pois, como ele ficará sempre enviando dados ao servidor, esse comunicação deve ser a mais leve possível para evitar sobrecargas por conta de mensagens muito pesadas.

Dessa forma, o protocolo foi estruturado da seguinte maneira:

`código_do_medidor;valor_da_medição;horário_da_medição` 

Basicamente, a mensagem é formada por três informações apenas, o código do medidor, seguido do valor da medição e por fim o horário em que foi feita a medição do consumo. Esses dados são enviados para o servidor através de um socket UDP onde apenas o dispositivo transmite.
<a id="secao5"></a>
## Materiais utilizados

[Mensagens HTTP](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Messages)

[Status de respostas](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status)

[Métodos de requisição](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Methods)

[Sockets em Java](https://www.devmedia.com.br/java-sockets-criando-comunicacoes-em-java/9465)


#### ⬆️ [Voltar ao topo](#inicio)

