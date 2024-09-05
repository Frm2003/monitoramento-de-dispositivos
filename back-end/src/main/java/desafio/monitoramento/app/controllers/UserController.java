//Autor: Felippe Ramos
//Criado em: 02/09/2024
//Modificado em:02/09/2024

package desafio.monitoramento.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.monitoramento.app.dtos.LoginDto;
import desafio.monitoramento.app.dtos.TokenDto;
import desafio.monitoramento.app.dtos.UserDto;
import desafio.monitoramento.app.infra.sercurity.TokenService;
import desafio.monitoramento.app.models.User;
import desafio.monitoramento.app.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/teste")
	public ResponseEntity<String> teste() {
		return ResponseEntity.status(HttpStatus.OK).body("teste");
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
		var userPassowrd = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.senha());
		var auth = authenticationManager.authenticate(userPassowrd);
		
		User user = (User) auth.getPrincipal();
		
		var token = tokenService.generateToken(user.getEmail());
		
		return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(token));
	}
	
	@PostMapping
	public ResponseEntity<User> insert(@RequestBody @Valid UserDto userDto) {
		if (userService.loadUserByUsername(userDto.email()) == null) { 
			return ResponseEntity.status(HttpStatus.CREATED).body(userService.insert(userDto));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}
