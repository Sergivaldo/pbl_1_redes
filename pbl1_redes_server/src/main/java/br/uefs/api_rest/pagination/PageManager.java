package br.uefs.api_rest.pagination;

import br.uefs.api_rest.model.ClientModel;
import br.uefs.api_rest.model.ConsumptionModel;

import java.util.LinkedList;
import java.util.List;

public class PageManager {
    private final List<ConsumptionModel> data;

    public PageManager(List<ConsumptionModel> data) {
        this.data = data;
    }

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
