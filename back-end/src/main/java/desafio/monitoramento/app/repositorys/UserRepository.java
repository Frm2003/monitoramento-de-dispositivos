//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.repositorys;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import desafio.monitoramento.app.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	UserDetails findByEmail(String email);
}
