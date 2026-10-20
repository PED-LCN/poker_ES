package engenharia.de.software.poker.api.repository;

import engenharia.de.software.poker.api.model.entity.Usuario;
import org.springframework.data.jpa.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.*;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Opitional<Usuario> findByEmail(String email);
}
