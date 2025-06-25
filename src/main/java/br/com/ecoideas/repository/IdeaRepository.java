package br.com.ecoideas.repository;

import br.com.ecoideas.model.Idea;
import br.com.ecoideas.model.StatusIdeia;
import br.com.ecoideas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    
    List<Idea> findByAutor(Usuario autor);
    List<Idea> findByStatus(StatusIdeia status);

   
}