package ar.utn.ccaffa.web;

import ar.utn.ccaffa.config.JwtUtil;
import ar.utn.ccaffa.model.dto.LoginRequest;
import ar.utn.ccaffa.model.dto.LoginResponseDto;
import ar.utn.ccaffa.model.entity.Usuario;
import ar.utn.ccaffa.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        
        String[] roles = userDetails.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .toArray(String[]::new);

        return ResponseEntity.ok(new LoginResponseDto(token, userDetails.getUsername(), roles, usuario.getNombre(), usuario.getId().toString()));
    }
} 