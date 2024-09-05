//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.repositorys;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import desafio.monitoramento.app.models.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID>{
	@Query(value = "select * from log_model lm where lm.id_dispositivo = ?", nativeQuery = true)
	List<Log> findAllByDeviceId(@Param("id") UUID id);
}
