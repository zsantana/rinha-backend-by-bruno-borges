package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestJson {
    
    @Test
    public void testPessoaToJson() throws JsonProcessingException {
        Pessoas pessoa = new Pessoas();
        //pessoa.setId(UUID.randomUUID());
        pessoa.setApelido("apelido");
        pessoa.setNome("nome");
        pessoa.setNascimento("nascimento");
        pessoa.setStack(Arrays.asList("Java", "Python"));

        // using jackson objectmapper, convert pessoa to json string
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pessoa);
        
        // using jackson objectmapper, convert json string to pessoa
        Pessoas pessoa2 = mapper.readValue(json, Pessoas.class);

        //assertEquals(pessoa, pessoa2);
    }

}
