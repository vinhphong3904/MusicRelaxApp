package com.example.musicapp.di;

import com.example.musicapp.core.datastore.UserDataStore;
import com.example.musicapp.core.network.ApiService;
import com.example.musicapp.domain.repository.AuthRepository;
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
public final class AppModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiProvider;

  private final Provider<UserDataStore> storeProvider;

  public AppModule_ProvideAuthRepositoryFactory(Provider<ApiService> apiProvider,
      Provider<UserDataStore> storeProvider) {
    this.apiProvider = apiProvider;
    this.storeProvider = storeProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(apiProvider.get(), storeProvider.get());
  }

  public static AppModule_ProvideAuthRepositoryFactory create(Provider<ApiService> apiProvider,
      Provider<UserDataStore> storeProvider) {
    return new AppModule_ProvideAuthRepositoryFactory(apiProvider, storeProvider);
  }

  public static AuthRepository provideAuthRepository(ApiService api, UserDataStore store) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthRepository(api, store));
  }
}
