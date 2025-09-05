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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Usuario usuarioLogado = usuarioRepository.findByEmail(userEmail);

        List<Idea> ideiasDoUsuario = ideaService.listarIdeiasPorAutor(usuarioLogado);
        model.addAttribute("ideiasDoUsuario", ideiasDoUsuario);
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        Map<UnidadeDeMedida, Double> impactoPessoal = ideaService.calcularImpactoPorAutor(usuarioLogado);

        model.addAttribute("impactoAgua", impactoPessoal.getOrDefault(UnidadeDeMedida.LITROS, 0.0));
        model.addAttribute("impactoEnergia", impactoPessoal.getOrDefault(UnidadeDeMedida.KWH, 0.0));
        model.addAttribute("impactoResiduos", impactoPessoal.getOrDefault(UnidadeDeMedida.KG, 0.0));

        return "dashboard";
    }
}