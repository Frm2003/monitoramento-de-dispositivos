//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.infra.sercurity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;

	public String generateToken(String cpf) {
		try {
			Algorithm alg = Algorithm.HMAC256(secret);
			return JWT.create().withIssuer("user").withSubject(cpf)
					.withExpiresAt(genExpirationDate()).sign(alg);

		} catch (JWTCreationException jwtCreationException) {
			throw new RuntimeException("Token generations failure", jwtCreationException);
		}
	}

	public String validateToken(String token) {
		try {
			Algorithm alg = Algorithm.HMAC256(secret);
			return JWT.require(alg).withIssuer("user").build().verify(token).getSubject();

		} catch (JWTVerificationException jwtVerificationException) {
			return "";
		}
	}

	private Instant genExpirationDate() {
		return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"));
	}
}