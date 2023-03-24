package br.uefs.api_rest.pagination;

import br.uefs.api_rest.model.ConsumptionModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Classe que cria paginas de uma determinada lista de dados
 */
public class PageManager {
    private final List<ConsumptionModel> data;

    public PageManager(List<ConsumptionModel> data) {
        this.data = data;
    }

    /**
     * Cria uma página com base no offset e limit passados pelo usuário
     * @param paginationOffset id do primeiro dado
     * @param paginationLimit quantidade de dados que serão pegos
     * @return Lista com os dados
     */
    public List<ConsumptionModel> createPage(int paginationOffset,int paginationLimit){
        List page;

        if (paginationOffset < data.size()) {
            if ((paginationOffset + paginationLimit) < data.size()) {
                page = data.subList(paginationOffset, paginationOffset + paginationLimit);
            }else{
                page = createPage();
            }
        }else{
            page = createPage();
        }
        return page;
    }

    /**
     * Cria uma página com base no offset e limit padrão
     * @return Lista com os dados
     */
    public List<ConsumptionModel> createPage(){
        List page;
        int default_limit = 10;

        if(data.isEmpty()){
            return new LinkedList();
        }

        if (data.size() < default_limit) {
            page = data.subList(0, data.size());
        } else {
            int default_offset = (data.size() - default_limit);
            page = data.subList(default_offset,default_offset + default_limit);
        }

        return page;
    }
}
