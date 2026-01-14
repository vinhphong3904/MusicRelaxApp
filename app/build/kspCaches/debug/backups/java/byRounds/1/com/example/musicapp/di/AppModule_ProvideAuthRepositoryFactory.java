package com.example.musicapp.di;

import com.example.musicapp.data.remote.AuthRemoteDataSource;
import com.example.musicapp.data.repository.AuthRepositoryInterface;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideAuthRepositoryFactory implements Factory<AuthRepositoryInterface> {
  private final Provider<AuthRemoteDataSource> remoteProvider;

  public AppModule_ProvideAuthRepositoryFactory(Provider<AuthRemoteDataSource> remoteProvider) {
    this.remoteProvider = remoteProvider;
  }

  @Override
  public AuthRepositoryInterface get() {
    return provideAuthRepository(remoteProvider.get());
  }

  public static AppModule_ProvideAuthRepositoryFactory create(
      Provider<AuthRemoteDataSource> remoteProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(remoteProvider);
  }

  public static AuthRepositoryInterface provideAuthRepository(AuthRemoteDataSource remote) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthRepository(remote));
  }
}
