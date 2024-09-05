//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.dtos;

import java.time.LocalDateTime;

import desafio.monitoramento.app.models.Dispositivo.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DispositivoDto(@NotBlank String name, @NotNull LocalDateTime lastPing, @NotBlank String location,
		Status status) {

}
