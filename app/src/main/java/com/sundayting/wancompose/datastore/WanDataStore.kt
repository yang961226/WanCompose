package com.sundayting.wancompose.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    fun bindDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.dataStore
    }

}