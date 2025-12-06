package com.web.eventos.services;

import org.springframework.stereotype.Service;

import com.web.eventos.entities.Local;
import com.web.eventos.entities.Organizacao;
import com.web.eventos.repositories.LocalRepository;

import java.util.List;

@Service
public class LocalService {
    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    public Local findById(Integer id) {
        return localRepository.findById(id).orElse(null);
    }

    public List<Local> findByOrganizacao(Organizacao organizacao) {
        return localRepository.findByOrganizacao(organizacao);
    }

    public List<Local> findAll() {
        return localRepository.findAll();
    }

    public List<String> findDistinctCidade() {
        return localRepository.findDistinctCidade();
    }

    public Local salvar(Local local) {
        return localRepository.save(local);
    }

    public Local atualizar(Local local) {
        Local existing = localRepository.findById(local.getId())
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado"));

        existing.setNome(local.getNome());
        existing.setEndereco(local.getEndereco());
        existing.setCidade(local.getCidade());
        existing.setEstado(local.getEstado());
        existing.setCep(local.getCep());
        existing.setCapacidade(local.getCapacidade());
        existing.setFacilidades(local.getFacilidades());

        return localRepository.save(existing);
    }

    public void excluir(Integer id) {
        localRepository.deleteById(id);
    }
}
