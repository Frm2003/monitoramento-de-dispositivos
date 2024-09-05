//Autor: Felippe Ramos
//Criado em: 03/09/2024
//Modificado em: 03/09/2024

package desafio.monitoramento.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desafio.monitoramento.app.dtos.LogDto;
import desafio.monitoramento.app.models.Dispositivo;
import desafio.monitoramento.app.models.Log;
import desafio.monitoramento.app.repositorys.DispositivoRepository;
import desafio.monitoramento.app.repositorys.LogRepository;

@Service
public class LogService {
	@Autowired
	private LogRepository logRepository;

	@Autowired
	private DispositivoRepository dispositivoRepository;
	
	public List<Log> getAllById(UUID id) {
		return this.logRepository.findAllByDeviceId(id);
	}
	
	public Log insert(LogDto logDto) throws Exception {
		Optional<Dispositivo> dispositivo = this.dispositivoRepository.findById(logDto.dispositivo());

		if (dispositivo.isEmpty())
			throw new Exception("Dispositivo não existe");
		
		Log log = new Log();
		log.setText(logDto.text());
		log.setDispositivo(dispositivo.get());

		return this.logRepository.save(log);
	}
	
	public List<Log> deleteByDeviceId(UUID idDevice) {
		List<Log> logs = this.logRepository.findAllByDeviceId(idDevice);
		
		for (Log log : logs) {
			log.setDispositivo(null);
			this.logRepository.delete(log);
		}
		
		return logs;
	}

}
