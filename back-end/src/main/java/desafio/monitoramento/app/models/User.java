//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_model")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false, unique = true, name = "email")
	private String email;

	@Column(nullable = false)
	private String senha;

	@Column(nullable = false, columnDefinition = "DATE")
	private LocalDateTime createOn;

	@Column(nullable = false, columnDefinition = "DATE")
	private LocalDateTime updatedOn;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "role")
	private UserRole role;

	public enum UserRole {
		USER("user");

		@Getter
		@Setter
		private String role;

		private UserRole(String role) {
			this.role = role;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role == UserRole.USER)
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		return null;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return nome;
	}

}
