package com.registroformazione.security.auth;


import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
      AuthenticationResponse resp = service.register(request);
      log.info("L'utente si è registrato correttamente.");
      log.debug("L'utente si è registrato correttamente: "+request.toString());
    return ResponseEntity.ok(resp);
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
      AuthenticationResponse resp = service.authenticate(request);
      log.info("L'utente si è autenticato correttamente.");
      log.debug("L'utente si è autenticato correttamente: "+request.toString());
    return ResponseEntity.ok(resp);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
    log.info("Token refresh eseguito.");
    log.debug("Token refresh eseguito: "+request.toString()+" - "+response.toString());
  }


}
