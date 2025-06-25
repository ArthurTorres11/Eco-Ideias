package br.com.ecoideas.service;

import br.com.ecoideas.model.*;
import br.com.ecoideas.repository.IdeaRepository;
import br.com.ecoideas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Idea criarNovaIdeia(Idea idea) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();
        Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado);
        if (autor == null) {
            throw new RuntimeException("Usuário logado não encontrado no banco de dados.");
        }
        idea.setAutor(autor);
        return ideaRepository.save(idea);
    }

    public List<Idea> listarTodasAsIdeias() {
        return ideaRepository.findAll();
    }

    public List<Idea> listarIdeiasPorAutor(Usuario autor) {
        return ideaRepository.findByAutor(autor);
    }

    public Idea buscarPorId(Long id) {
        return ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ideia não encontrada com o ID: " + id));
    }

    public Idea atualizarStatus(Long id, StatusIdeia novoStatus) {
        Idea ideaExistente = buscarPorId(id);
        ideaExistente.setStatus(novoStatus);
        return ideaRepository.save(ideaExistente);
    }

    public void deletarIdeia(Long id) {
        if (!ideaRepository.existsById(id)) {
            throw new RuntimeException("Ideia não encontrada com o ID: " + id + ". Não foi possível deletar.");
        }
        ideaRepository.deleteById(id);
    }

    /**
     * Calcula a soma total do impacto para cada unidade de medida,
     * considerando apenas as ideias APROVADAS ou IMPLEMENTADAS de um autor específico.
     * @param autor O usuário para o qual o impacto será calculado.
     * @return Um Mapa contendo a soma do impacto para cada unidade.
     */
    public Map<UnidadeDeMedida, Double> calcularImpactoPorAutor(Usuario autor) {
        List<Idea> ideiasDoAutor = ideaRepository.findByAutor(autor);

        return ideiasDoAutor.stream()
                .filter(idea -> idea.getStatus() == StatusIdeia.APROVADA || idea.getStatus() == StatusIdeia.IMPLEMENTADA)
                .filter(idea -> idea.getValorDoImpacto() != null && idea.getUnidadeDeMedida() != null)
                .collect(groupingBy(
                        Idea::getUnidadeDeMedida,
                        summingDouble(Idea::getValorDoImpacto)
                ));
    }
}