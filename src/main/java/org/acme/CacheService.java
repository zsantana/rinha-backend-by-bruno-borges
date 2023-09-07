package org.acme;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class CacheService {

    private Set<String> apelidos = new HashSet<>(100000);

    private LinkedHashMap<UUID, Pessoas> pessoas = new LinkedHashMap<>(100000);

    public boolean apelidoExists(String apelido) {
        return apelidos.contains(apelido);
    }

    public Pessoas getPessoa(String id) {
        return pessoas.get(UUID.fromString(id));
    }

    public synchronized void insertPessoa(Pessoas pessoa) {
        pessoas.put(pessoa.getId(), pessoa);
        apelidos.add(pessoa.getApelido());
    }

    public synchronized List<Pessoas> search(String termo) {
        return pessoas.values().stream()
                .filter(p -> p.getApelido().contains(termo) 
                    || p.getNome().contains(termo) 
                    || p.getStack().contains(termo)
                )
                .limit(50)
                .collect(Collectors.toList());
    }
    
}
