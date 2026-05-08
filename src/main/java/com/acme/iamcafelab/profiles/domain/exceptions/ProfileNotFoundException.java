package com.acme.iamcafelab.profiles.domain.exceptions;

public class ProfileNotFoundException extends RuntimeException {

  public ProfileNotFoundException(Long profileId) {
    super("Perfil no encontrado");
  }

  public ProfileNotFoundException(String email) {
    super("Perfil no encontrado para el email indicado");
  }
}