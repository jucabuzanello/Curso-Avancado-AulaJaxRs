package com.juca.projeto_modular.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.juca.projeto_modular.data.UsuarioRepository;
import com.juca.projeto_modular.model.Usuario;
import com.juca.projeto_modular.service.UsuarioRegistration;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;


@Path("/usuarios")
@RequestScoped
public class UsuarioResource {
    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private UsuarioRepository repository;

    @Inject
    UsuarioRegistration registration;
    
   // @Context
  //  private UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response listAllUsuarios(
            @DefaultValue("0") @QueryParam("offset") int offset, 
            @DefaultValue("50") @QueryParam("limit") int limit, 
            @DefaultValue("+nome") @QueryParam("sort") String sort) {
        return Response.ok(repository.findAllOrderedByName(offset, limit, sort)).build();
    }

    @GET
    @Path("header")
    @Produces({MediaType.APPLICATION_JSON})
    public Response pegarHttpHeaders(@Context HttpHeaders headers) {
        Map<String,String> dados = new HashMap<>();
        dados.put("strHeader",headers.getHeaderString("testeheader"));
        dados.put("headerInteiro",headers.toString());
        return Response.ok(dados).build();
    }
    
    @GET
    @Path("uri")
    @Produces({MediaType.APPLICATION_JSON})
    public Response pegarUriDetails(@Context UriInfo uriInfo) {
        Map<String, String> dados = new HashMap<>();
        dados.put("host",uriInfo.getBaseUri().getHost());
        dados.put("path", uriInfo.getPath());
        dados.put("parameters", uriInfo.getQueryParameters().toString());
        return Response.ok(dados).build();
    }
    
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Usuario lookupUsuarioById(@PathParam("id") long id) {
        Usuario usuario = repository.findById(id);
        if (usuario == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return usuario;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUsuario(Usuario usuario) {
        return createUpdate(usuario);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response updateUsuario(Usuario usuario) {
        return createUpdate(usuario);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
    @Path("{id}")
    public Response removeUsuario(@PathParam("id") Long idUsuario) {
        Response.ResponseBuilder builder = null;
        try {
            registration.remove(lookupUsuarioById(idUsuario));
            builder = Response.ok();
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);        
        }
        return builder.build();
    }

    private Response createUpdate(Usuario usuario) {
        Response.ResponseBuilder builder = null;

        try {
            // Validates usuario using bean validation
            validateUsuario(usuario);

            registration.register(usuario);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();     
    }

    private void validateUsuario(Usuario usuario) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(usuario.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    public boolean emailAlreadyExists(String email) {
        Usuario usuario = null;
        try {
            usuario = repository.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }
        return usuario != null;
    }
}
