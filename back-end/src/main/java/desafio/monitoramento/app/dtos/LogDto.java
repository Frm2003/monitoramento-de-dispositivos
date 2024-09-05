package desafio.monitoramento.app.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogDto(@NotBlank String text, @NotNull UUID dispositivo) {

}
