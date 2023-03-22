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

## Seções 

&nbsp;&nbsp;&nbsp;[**1.** Diagrama do projeto](#secao1)

&nbsp;&nbsp;&nbsp;[**2.** Rotas utilizadas](#secao2)

## Diagrama do projeto

![Diagrama pbl1png](https://user-images.githubusercontent.com/72475500/226783949-0c05ae80-5805-47bc-a7bc-76a4a9e8ebb6.png)

O diagrama acima mostra o fluxo das mensagens trocadas entre clientes e servidor. No sistema foi utilizado os sockets para fazer a comunicação entre cada uma
das entidades do sistema(cliente, servidor e medidor). No socket da comunicação entre medidor e servidor foi utilizado o protocolo UDP já que a troca de mensagens entre estes deve ser menos custoza, pois, como o medidor sempre enviará mensagens de forma síncrona(em um determinado intervalo de tempo).

#### ⬆️ [Voltar ao topo](#inicio)

