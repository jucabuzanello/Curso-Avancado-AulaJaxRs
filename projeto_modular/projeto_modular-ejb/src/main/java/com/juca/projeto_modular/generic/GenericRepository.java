package com.juca.projeto_modular.generic;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


public class GenericRepository<T> {

    @Inject
    private EntityManager em;

    public T findById(Class<T> entityClass, Long id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAllOrderedByName(Class<T> entityClass, int offset, int limit, String sort) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(entityClass);
        Root<T> entidade = criteria.from(entityClass);
        //http://localhost:8080/projeto_modular-web/api/usuarios?sort=nome+asc
        //http://localhost:8080/projeto_modular-web/api/usuarios?sort=nome+desc
        if (sort.indexOf("desc") > 0) {
            sort = sort.replaceAll("desc", "").trim();
            criteria.select(entidade).orderBy(cb.desc(entidade.get(sort)));
        } else {
            sort = sort.replaceAll("asc", "").trim();
            criteria.select(entidade).orderBy(cb.asc(entidade.get(sort)));
        }
        return getEntityManager().createQuery(criteria).getResultList();
    }
    
    public T findByFilter(Class<T> entityClass, String colunaFilter, String filter) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(entityClass);
        Root<T> entidade = criteria.from(entityClass);
        criteria.select(entidade).where(cb.equal(entidade.get(colunaFilter), filter));
        return getEntityManager().createQuery(criteria).getSingleResult();
    }
    
    public EntityManager getEntityManager() {
        return em;
    }
}
