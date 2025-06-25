package br.com.ecoideas.controller;

import br.com.ecoideas.model.Idea;
import br.com.ecoideas.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ideias")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/novo")
    public String mostrarFormularioDeIdeia(Model model) {
        model.addAttribute("idea", new Idea());
        return "ideias/formulario";
    }

    @PostMapping("/salvar")
    public String salvarIdeia(@ModelAttribute("idea") Idea idea) {
        ideaService.criarNovaIdeia(idea);
        return "redirect:/dashboard";
    }
}