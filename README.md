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

**API REST** -

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

## Rotas utilizadas

|      **Rota**      	| **Métodos aceitos** 	|              **Parâmetros de consulta**             	|                                           **Descrição**                                          	|
|:------------------:	|:-------------------:	|:---------------------------------------------------:	|:------------------------------------------------------------------------------------------------:	|
|     */cliente*     	|    `POST`, `GET`    	|                   id(obrigatório)                   	|                        Rota utilizada para consultar e cadastrar clientes                        	|
| */consumo_energia* 	|        `GET`        	| id(obrigatório), offset(opcional) e limit(opcional) 	|         Rota utilizada para consultar consumo de energia total e em determinados horários        	|
|      */fatura*     	|        `GET`        	|                   id(obrigatório)                   	|                           Rota utilizada para gerar fatura de pagamento                          	|
|  */alerta_consumo* 	|        `GET`        	|                   id(obrigatório)                   	| Rota utilizada para consultar alertas de consumo excessivo e grande variação na conta do usuário 	|

#### ⬆️ [Voltar ao topo](#inicio)

