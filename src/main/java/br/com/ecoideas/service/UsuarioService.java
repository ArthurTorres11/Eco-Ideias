// src/main/java/br/com/ecoideas/service/UsuarioService.java
package br.com.ecoideas.service;

import br.com.ecoideas.model.Usuario;
import br.com.ecoideas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// A mudança principal: "implements UserDetailsService"
// Isso é o "contrato" que diz ao Spring Security para usar este serviço para o login.
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- LÓGICA DE LOGIN (EXIGIDA PELO UserDetailsService) ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Este método é chamado AUTOMATICAMENTE pelo Spring Security quando alguém tenta logar.
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
        }
        // Retorna um objeto UserDetails que o Spring usa para validar a senha e as permissões.
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                getAuthorities(usuario) // Método que busca as permissões/roles
        );
    }

    // Método auxiliar para converter nossas Roles em um formato que o Spring Security entende
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        return Stream.of(usuario.getRole())
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }


    // --- LÓGICA DO CRUD (CRIAR, LER, ATUALIZAR, DELETAR) ---

    public Usuario cadastrarNovoUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("Este email já está em uso.");
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("Este CPF já está cadastrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id);
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setRole(usuarioAtualizado.getRole());
        usuarioExistente.setRegistroEmpresa(usuarioAtualizado.getRegistroEmpresa());
        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id + ". Não foi possível deletar.");
        }
        usuarioRepository.deleteById(id);
    }

    public void trocarSenha(Long id, String senhaAntiga, String novaSenha) {
        Usuario usuario = buscarPorId(id);
        if (!passwordEncoder.matches(senhaAntiga, usuario.getSenha())) {
            throw new RuntimeException("A senha antiga está incorreta.");
        }
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}