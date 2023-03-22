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

#### ⬆️ [Voltar ao topo](#inicio)

