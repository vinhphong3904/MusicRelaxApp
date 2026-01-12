package com.example.musicapp.presentation.auth;

import com.example.musicapp.domain.usecase.LoginUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<LoginUseCase> loginUseCaseProvider;

  public AuthViewModel_Factory(Provider<LoginUseCase> loginUseCaseProvider) {
    this.loginUseCaseProvider = loginUseCaseProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(loginUseCaseProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<LoginUseCase> loginUseCaseProvider) {
    return new AuthViewModel_Factory(loginUseCaseProvider);
  }

  public static AuthViewModel newInstance(LoginUseCase loginUseCase) {
    return new AuthViewModel(loginUseCase);
  }
}
