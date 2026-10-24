package bookleaf.supportportal.controller;


	import org.springframework.security.authentication.BadCredentialsException;
	import org.springframework.security.crypto.password.PasswordEncoder;
	import org.springframework.web.bind.annotation.*;
	import bookleaf.supportportal.dto.LoginRequest;
	import bookleaf.supportportal.dto.LoginResponse;
	import bookleaf.supportportal.entity.User;
	import bookleaf.supportportal.repository.UserRepository;
	import bookleaf.supportportal.security.JwtService;
	import jakarta.validation.Valid;

	@RestController
	@RequestMapping("/api/auth")
	public class AuthController {

	    private final UserRepository userRepo;
	    private final PasswordEncoder encoder;
	    private final JwtService jwtService;

	    public AuthController(UserRepository userRepo, PasswordEncoder encoder, JwtService jwtService) {
	        this.userRepo = userRepo;
	        this.encoder = encoder;
	        this.jwtService = jwtService;
	    }

	    @PostMapping("/login")
	    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
	        User user = userRepo.findByEmail(req.email().trim())
	                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
	        if (!encoder.matches(req.password(), user.getPasswordHash())) {
	            throw new BadCredentialsException("Invalid email or password");
	        }
	        return new LoginResponse(jwtService.generateToken(user),
	                user.getId(), user.getName(), user.getRole().name());
	    }
	}


