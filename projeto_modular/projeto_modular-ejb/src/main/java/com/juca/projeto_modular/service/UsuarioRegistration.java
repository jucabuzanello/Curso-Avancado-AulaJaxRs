package com.juca.projeto_modular.service;

import com.juca.projeto_modular.generic.GenericRegistration;
import com.juca.projeto_modular.model.Usuario;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class UsuarioRegistration extends GenericRegistration<Usuario>{

    @Inject
    private Logger log;

    @Inject
    private Event<Usuario> usuarioEventSrc;

    public void registrar(Usuario usuario) throws Exception {
        if (usuario.getId() == null) {
            log.info("Inserindo " + usuario.getNome());
            inserir(usuario);
        } else {
            log.info("Atualizando " + usuario.getNome());
            atualizar(usuario);
        }
        usuarioEventSrc.fire(usuario);
    }

  public void remover(Usuario usuario) throws Exception {
        log.info("Removendo " + usuario.getNome());
        remover(usuario);
        usuarioEventSrc.fire(usuario);
    }    
}
