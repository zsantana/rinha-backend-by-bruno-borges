package org.acme;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
public class PessoaResource {

    private static final Logger logger = LoggerFactory.getLogger(PessoaResource.class);


    @Inject
    CacheService cache;


    @GET
    @Path("/pessoas")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> findTop50(@QueryParam("t") String termo) {
        
        if (termo == null || termo.isBlank()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        List<Pessoas> foundInCache = cache.search(termo);
        // if (foundInCache != null){

        // }else{
        //     Uni<List<Pessoa>> r = Pessoa.find("busca like '%' || ?1 || '%'", termo).page(0, 50).list();
        // }
        return Uni.createFrom().item(Response.ok(foundInCache).build());
    }

    @POST
    @Path("/pessoas")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> create(Pessoas pessoa) {

        String apelido = pessoa.getApelido();
        String nome = pessoa.getNome();

        if (apelido == null || apelido.isBlank() || apelido.length() > 32
                || nome == null || nome.isBlank() || nome.length() > 100 
                || invalidStack(pessoa.getStack())
                ) {
            //logger.info("### Apelido fora do padrao permitido");
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        if (pessoaByApelidoExists(pessoa.getApelido())) {
            return Uni.createFrom().item(Response.status(422).build());
        }

        var uuid = UUID.randomUUID();
        //logger.info("Gerando UUID: {}", uuid);

        pessoa.setId(uuid);
        cache.insertPessoa(pessoa);

        return Panache
                .withTransaction(pessoa::persist)
                .replaceWith(Response.status(Status.CREATED).entity(pessoa)
                .header("Location", "/pessoas/" + uuid.toString())
                .build());
                            
    }

    
    @GET
    @Path("/pessoas/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> get(@PathParam("id") String id) {
        
        var foundInCache = cache.getPessoa(id);
        //logger.info("Buscando ID {}", id);

        if (foundInCache != null) {
            //logger.info("Registro encontrado em Cache: " + id);
            return Uni.createFrom().item(Response.ok(foundInCache).build());
        } else {
            //logger.info("Buscando registro do banco de dados: " + id);
            return Panache
                    .withTransaction(() -> Pessoas.<Pessoas> findById(UUID.fromString(id)))
                    .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                    .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
            
        }
        
    }

   
    @GET
    @Path("/contagem-pessoas")
    @WithSession
    public Uni<Long> count() {
        return Pessoas.count();
    }

    private boolean pessoaByApelidoExists(String apelido) {
        return (cache.apelidoExists(apelido)) ;
    }

    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(";")) : emptyList();
    }


    @POST
    @Path("/pessoas-mock")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> create_mock(Pessoas pessoa) {

        var uuid = UUID.randomUUID();
        //logger.info("Gerando UUID: {}", uuid);

        pessoa.setId(uuid);
        cache.insertPessoa(pessoa);

        return Panache
                .withTransaction(pessoa::persist)
                .replaceWith(Response.status(Status.CREATED).entity(pessoa)
                //.header("Location", "/pessoa/" + uuid.toString())
                .build());
                            
    }


    private boolean invalidStack(List<String> stack) {
        if (stack == null) {
            return false;
        }

        for (String s : stack) {
            if (s.length() > 32) {
                return true;
            }
        }

        return false;
    }

}
