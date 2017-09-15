package com.juca.projeto_modular.data;

import com.juca.projeto_modular.generic.GenericRepository;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.juca.projeto_modular.model.Usuario;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class UsuarioRepository extends GenericRepository<Usuario> {
    
    public Usuario findById(Long id) {
        return findById(Usuario.class, id);
    }

    public Usuario findByEmail(String email) {
        return findByFilter(Usuario.class, "email", email);
    }

    public List<Usuario> findAllOrderedByName(int offset, int limit, String sort) {
        return findAllOrderedByName(Usuario.class, offset, limit, sort);
    }
}
