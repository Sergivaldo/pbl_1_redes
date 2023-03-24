package br.uefs.util;

import br.uefs.api_rest.model.ClientModel;

import java.util.List;

public class IdGenerator {

    /**
     * Gera um id para um cliente novo com base no tamanho da lista de clientes
     * @param clients Lista de clientes
     * @return Retorna inteiro com id do próximo cliente
     */
    public static int generateClientId(List<ClientModel> clients){
        return clients.size();
    }

    /**
     * Gera um id para o medidor com os argumentos passados como parâmetro usando hashCode
     * @param args Argumentos que serão utilizados para gerar o código do medidor
     * @return Inteiro com o código do novo medidor
     */
    public static int generateSmartMeterId(String[] args){
        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg);
        }

        int id = builder.toString().hashCode();
        return  id < 0? id * -1: id;
    }
}
