package br.com.ecoideas.controller;

import br.com.ecoideas.model.Idea;
import br.com.ecoideas.model.UnidadeDeMedida;
import br.com.ecoideas.model.Usuario;
import br.com.ecoideas.repository.UsuarioRepository;
import br.com.ecoideas.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        // Pega o email do usuário logado no sistema
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Busca o objeto completo do usuário no banco
        Usuario usuarioLogado = usuarioRepository.findByEmail(userEmail);

        // Usa o IdeaService para buscar APENAS as ideias daquele usuário
        List<Idea> ideiasDoUsuario = ideaService.listarIdeiasPorAutor(usuarioLogado);

        // Adiciona a lista de ideias reais ao Model, para o Thymeleaf usar
        model.addAttribute("ideiasDoUsuario", ideiasDoUsuario);

        // Adiciona o nome do usuário ao model para uma saudação personalizada
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());

        // --- LÓGICA PARA O PAINEL DE IMPACTO PESSOAL ---
        Map<UnidadeDeMedida, Double> impactoPessoal = ideaService.calcularImpactoPorAutor(usuarioLogado);

        model.addAttribute("impactoAgua", impactoPessoal.getOrDefault(UnidadeDeMedida.LITROS, 0.0));
        model.addAttribute("impactoEnergia", impactoPessoal.getOrDefault(UnidadeDeMedida.KWH, 0.0));
        model.addAttribute("impactoResiduos", impactoPessoal.getOrDefault(UnidadeDeMedida.KG, 0.0));

        return "dashboard";
    }
}