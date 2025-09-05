package br.com.ecoideas.controller;

import br.com.ecoideas.model.Usuario;
import br.com.ecoideas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String mostrarFormularioDeCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        usuarioService.cadastrarNovoUsuario(usuario);
        return "redirect:/usuarios";
    }
}