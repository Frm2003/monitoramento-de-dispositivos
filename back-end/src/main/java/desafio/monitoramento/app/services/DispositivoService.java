//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em: 04/09/2024

/*
 * Histórico de modificação: 
 * 	- adição do método getAllDevicesInLocalnet | data: 03/09/2024
 *  - adição do método validation | data: 04/09/2024
 * */

package desafio.monitoramento.app.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import desafio.monitoramento.app.dtos.DispositivoDto;
import desafio.monitoramento.app.models.Dispositivo;
import desafio.monitoramento.app.models.Dispositivo.Status;
import desafio.monitoramento.app.models.Log;
import desafio.monitoramento.app.repositorys.DispositivoRepository;
import desafio.monitoramento.app.repositorys.LogRepository;

@Service
public class DispositivoService {
	@Autowired
	private DispositivoRepository dispositivoRepository;
	
	@Autowired
	private LogRepository logRepository;

	public List<Dispositivo> getAll() {
		return this.dispositivoRepository.findAll();
	}

	public Dispositivo getById(UUID id) throws Exception {
		Optional<Dispositivo> optional = this.dispositivoRepository.findById(id);

		if (optional.isEmpty())
			throw new Exception("Dispositivo não encontrado.");

		return optional.get();
	}

	public Dispositivo insert(DispositivoDto dispositivoDto) {
		Dispositivo dispositivoModel = new Dispositivo();

		dispositivoModel.setName(dispositivoDto.name());
		dispositivoModel.setLastPing(dispositivoDto.lastPing());
		dispositivoModel.setLocation(dispositivoDto.location());
		dispositivoModel.setStatus(dispositivoDto.status());

		return this.dispositivoRepository.save(dispositivoModel);
	}

	public Dispositivo updateById(UUID id, DispositivoDto dispositivoDto) throws Exception {
		Dispositivo dispositivoModel = this.getById(id);

		dispositivoModel.setName(dispositivoDto.name());
		dispositivoModel.setLastPing(dispositivoDto.lastPing());
		dispositivoModel.setLocation(dispositivoDto.location());
		dispositivoModel.setStatus(dispositivoDto.status());

		return this.dispositivoRepository.save(dispositivoModel);
	}

	public Dispositivo deleteById(UUID id) throws Exception {
		Dispositivo dispositivo = this.getById(id);
		
		List<Log> logs = this.logRepository.findAllByDeviceId(id);

		for (Log log : logs) {
			log.setDispositivo(null);
			this.logRepository.delete(log);
		}

		this.dispositivoRepository.delete(dispositivo);

		return dispositivo;
	}

	public List<DispositivoDto> getAllDevicesInLocalnet() {
		String command = "nmap -sP 192.168.15.0/24";

		List<DispositivoDto> dispositivosEncontrados = new ArrayList<>();
		List<DispositivoDto> dispositivosNaoRegistrados = new ArrayList<>();

		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			Pattern hostPattern = Pattern.compile("Nmap scan report for (\\S+)");

			while ((line = reader.readLine()) != null) {
				Matcher hostMatcher = hostPattern.matcher(line);

				if (hostMatcher.find()) {
					String hostName = hostMatcher.group(1);
					DispositivoDto dispositivo = new DispositivoDto(hostName, LocalDateTime.now(), "Unknown Location",
							Status.ATIVO);
					dispositivosEncontrados.add(dispositivo);
				}
			}

			reader.close();
			process.waitFor();

			// Obter todos os dispositivos registrados no banco
			List<Dispositivo> dispositivosRegistrados = dispositivoRepository.findAll();

			// Filtrar dispositivos não registrados
			List<String> nomesRegistrados = dispositivosRegistrados.stream().map(Dispositivo::getName).toList();

			dispositivosNaoRegistrados = dispositivosEncontrados.stream()
					.filter(dispositivo -> !nomesRegistrados.contains(dispositivo.name())).toList();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dispositivosNaoRegistrados;
	}

	public List<Dispositivo> validarDispositivosInativosNaRede() {
	    List<Dispositivo> dispositivosRegistrados = dispositivoRepository.findAll();
	    
	    List<DispositivoDto> dispositivosEncontrados = getAllDevicesInLocalnetWithoutFilter();

	    List<String> nomesEncontrados = dispositivosEncontrados.stream()
	            .map(DispositivoDto::name)
	            .collect(Collectors.toList());

	    List<Dispositivo> dispositivosInativosEncontradosNaRede = dispositivosRegistrados.stream()
	            .filter(dispositivo -> dispositivo.getStatus() == Status.INATIVO)
	            .filter(dispositivo -> nomesEncontrados.contains(dispositivo.getName()))
	            .collect(Collectors.toList());

	    return dispositivosInativosEncontradosNaRede;
	} 
	
	public List<Dispositivo> validaDispositivosAtivosNaRede() {
		// Obter todos os dispositivos registrados no banco de dados
	    List<Dispositivo> dispositivosRegistrados = dispositivoRepository.findAll();
	    
	    // Obter todos os dispositivos encontrados na rede
	    List<DispositivoDto> dispositivosEncontrados = getAllDevicesInLocalnetWithoutFilter();

	    // Obter uma lista dos nomes dos dispositivos encontrados na rede
	    List<String> nomesEncontrados = dispositivosEncontrados.stream()
	            .map(DispositivoDto::name)
	            .collect(Collectors.toList());

	    // Filtrar dispositivos registrados que não estão na lista de encontrados
	    List<Dispositivo> dispositivosNaoEncontrados = dispositivosRegistrados.stream()
	            .filter(dispositivo -> !nomesEncontrados.contains(dispositivo.getName()))
	            .collect(Collectors.toList());

	    return dispositivosNaoEncontrados;
	}
	
	private List<DispositivoDto> getAllDevicesInLocalnetWithoutFilter() {
	    String command = "nmap -sP 192.168.15.0/24";

	    List<DispositivoDto> dispositivosEncontrados = new ArrayList<>();

	    try {
	        // Executa o comando nmap
	        Process process = Runtime.getRuntime().exec(command);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;

	        // Expressão regular para capturar o nome do host
	        Pattern hostPattern = Pattern.compile("Nmap scan report for (\\S+)");

	        // Lê a saída do comando nmap linha por linha
	        while ((line = reader.readLine()) != null) {
	            Matcher hostMatcher = hostPattern.matcher(line);

	            if (hostMatcher.find()) {
	                String hostName = hostMatcher.group(1);
	                // Cria um objeto DispositivoDto para cada host encontrado
	                DispositivoDto dispositivo = new DispositivoDto(hostName, LocalDateTime.now(), "Unknown Location",
	                        Status.ATIVO);
	                dispositivosEncontrados.add(dispositivo);
	            }
	        }

	        // Fecha o BufferedReader e aguarda o término do processo
	        reader.close();
	        process.waitFor();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // Retorna todos os dispositivos encontrados
	    return dispositivosEncontrados;
	}


}
