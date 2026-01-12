package com.example.musicapp.core.network;

import android.content.SharedPreferences;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<SharedPreferences> sharedPreferencesProvider;

  public AuthInterceptor_Factory(Provider<SharedPreferences> sharedPreferencesProvider) {
    this.sharedPreferencesProvider = sharedPreferencesProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(sharedPreferencesProvider.get());
  }

  public static AuthInterceptor_Factory create(
      Provider<SharedPreferences> sharedPreferencesProvider) {
    return new AuthInterceptor_Factory(sharedPreferencesProvider);
  }

  public static AuthInterceptor newInstance(SharedPreferences sharedPreferences) {
    return new AuthInterceptor(sharedPreferences);
  }
}
