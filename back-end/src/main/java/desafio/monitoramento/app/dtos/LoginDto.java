//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
		@NotBlank String email,
		@NotBlank String senha
	) {
		
	}
