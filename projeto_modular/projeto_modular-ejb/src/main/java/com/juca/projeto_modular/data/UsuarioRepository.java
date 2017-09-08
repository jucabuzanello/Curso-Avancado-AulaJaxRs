package com.juca.projeto_modular.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.juca.projeto_modular.model.Usuario;

@ApplicationScoped
public class UsuarioRepository {

    @Inject
    private EntityManager em;

    public Usuario findById(Long id) {
        return em.find(Usuario.class, id);
    }

    public Usuario findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = criteria.from(Usuario.class);
        criteria.select(usuario).where(cb.equal(usuario.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Usuario> findAllOrderedByName(int offset, int limit, String sort) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = criteria.from(Usuario.class);
        System.out.println("sort: " + sort);
        if (sort.substring(0, 1).equals("-")) {
           criteria.select(usuario).orderBy(cb.desc(usuario.get("nome")));
        } else {
            criteria.select(usuario).orderBy(cb.asc(usuario.get("nome")));
        }
        return em.createQuery(criteria).getResultList();
    }
}
