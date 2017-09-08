package com.juca.projeto_modular.service;

import com.juca.projeto_modular.model.Usuario;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class UsuarioRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Usuario> usuarioEventSrc;

    public void register(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            log.info("Inserindo " + usuario.getNome());
            em.persist(usuario);
        } else {
            log.info("Atualizando " + usuario.getNome());
            em.merge(usuario);
        }
        usuarioEventSrc.fire(usuario);
    }

  public void remove(Usuario usuario) throws Exception {
        log.info("Removendo " + usuario.getNome());
        Object registro = em.merge(usuario);
        em.remove(registro);
        usuarioEventSrc.fire(usuario);
    }    
}
