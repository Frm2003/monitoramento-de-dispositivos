//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em: 04/09/2024

/*
 * Histórico de modificação: 
 * 	- Adição dos métodos HTTP | data: 03/09/2024
 *  - Adição do método getAllDevicesInLocalnet | data: 03/09/2024
 *  - Adição do método validação da rede | data: 04/09/2024
 * */

package desafio.monitoramento.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.monitoramento.app.dtos.DispositivoDto;
import desafio.monitoramento.app.models.Dispositivo;
import desafio.monitoramento.app.services.DispositivoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/device")
public class DispositivoController {
	@Autowired
	private DispositivoService dispositivoService;
	
	@GetMapping
	public ResponseEntity<List<Dispositivo>> getAllDevices() {
		return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.getAll());
	}
	
	@GetMapping("/unregistred")
	public ResponseEntity<List<DispositivoDto>> getAllDevicesInLocalnet() {
		return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.getAllDevicesInLocalnet());
	}
	
	@GetMapping("/validateAtivo")
	public ResponseEntity<List<Dispositivo>> validateActiveDevices() {
		return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.validaDispositivosAtivosNaRede());
	}
	
	@GetMapping("/validateInativo")
	public ResponseEntity<List<Dispositivo>> validateInativeDevices() {
		return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.validarDispositivosInativosNaRede());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getById(@PathVariable("id") UUID id) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.getById(id));
		} catch (Exception error) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
	}
	
	@PostMapping
	public ResponseEntity<Object> insert(@RequestBody @Valid DispositivoDto dispositivoDto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(this.dispositivoService.insert(dispositivoDto));
		} catch (Exception error) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateById(@PathVariable("id") UUID id, @RequestBody @Valid DispositivoDto dispositivoDto) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.updateById(id, dispositivoDto));
		} catch (Exception error) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteById(@PathVariable("id") UUID id) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.dispositivoService.deleteById(id));
		} catch (Exception error) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
		}
	}
	
}
