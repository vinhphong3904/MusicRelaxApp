package com.example.musicapp.core.datastore;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UserDataStore_Factory implements Factory<UserDataStore> {
  private final Provider<Context> contextProvider;

  public UserDataStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserDataStore get() {
    return newInstance(contextProvider.get());
  }

  public static UserDataStore_Factory create(Provider<Context> contextProvider) {
    return new UserDataStore_Factory(contextProvider);
  }

  public static UserDataStore newInstance(Context context) {
    return new UserDataStore(context);
  }
}
