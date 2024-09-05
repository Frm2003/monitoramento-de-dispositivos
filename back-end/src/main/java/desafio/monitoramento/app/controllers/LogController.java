//Autor: Felippe Ramos
//Criado em: 03/09/2024
//Modificado em: 03/09/2024

package desafio.monitoramento.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.monitoramento.app.dtos.LogDto;
import desafio.monitoramento.app.models.Log;
import desafio.monitoramento.app.services.LogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/log")
public class LogController {
	@Autowired
	private LogService logService;
	
	@GetMapping("/{id}")
	public ResponseEntity<List<Log>> selectAll(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK).body(this.logService.getAllById(id));
	}
	
	@PostMapping
	public ResponseEntity<Object> insert(@RequestBody @Valid LogDto logDto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(this.logService.insert(logDto));
		} catch (Exception error) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
