package com.juca.projeto_modular.generic;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

public class GenericRegistration<T> {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    public void inserir(T entidade) throws Exception {
        em.persist(entidade);
    }
    
    public T atualizar(T entidade) throws Exception {
        return em.merge(entidade);
    }
    
    public void remover(T entidade) throws Exception {
        Object registro = em.merge(entidade);
        em.remove(registro);
    }    
}
