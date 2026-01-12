package com.example.musicapp.data.repository;

import com.example.musicapp.core.datastore.UserDataStore;
import com.example.musicapp.core.network.ApiService;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<ApiService> apiProvider;

  private final Provider<UserDataStore> userStoreProvider;

  public AuthRepositoryImpl_Factory(Provider<ApiService> apiProvider,
      Provider<UserDataStore> userStoreProvider) {
    this.apiProvider = apiProvider;
    this.userStoreProvider = userStoreProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), userStoreProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<ApiService> apiProvider,
      Provider<UserDataStore> userStoreProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, userStoreProvider);
  }

  public static AuthRepositoryImpl newInstance(ApiService api, UserDataStore userStore) {
    return new AuthRepositoryImpl(api, userStore);
  }
}
