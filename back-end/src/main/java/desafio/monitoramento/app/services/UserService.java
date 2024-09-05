//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import desafio.monitoramento.app.dtos.UserDto;
import desafio.monitoramento.app.models.User;
import desafio.monitoramento.app.repositorys.UserRepository;

@Service
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	public User insert(UserDto userDto) {
		String encryptedPasswd = new BCryptPasswordEncoder().encode(userDto.senha());
		User user = new User();
		
		user.setNome(userDto.nome());
		user.setEmail(userDto.email());
		user.setSenha(encryptedPasswd);
		user.setRole(userDto.role());
		user.setCreateOn(LocalDateTime.now());
		user.setUpdatedOn(LocalDateTime.now());
		
		return this.userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (userRepository.findByEmail(username) != null) {
			return userRepository.findByEmail(username);			
		}
		return null;
	}
}
