package com.safesoft.safemobile.backend.db.remote;

import android.util.Log;

import com.safesoft.safemobile.backend.repository.PreferencesRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class RemoteDBRepo {
    private PreferencesRepository preferencesRepository;
    private Connection connection;

    private static final String TAG = RemoteDBRepo.class.getSimpleName();

    private static RemoteDBRepo remoteDBRepo;

    private RemoteDBRepo() {
    }

    private HashMap<String, String> loadConfigs() {
        String server = remoteDBRepo.preferencesRepository.getLocalServerIp();
        String path = remoteDBRepo.preferencesRepository.getDBPath();
        HashMap<String, String> configs = new HashMap<>();
        configs.put("path", path);
        configs.put("server", server);
        return configs;
    }

    public static void initialize() {
        if (remoteDBRepo == null)
            remoteDBRepo = new RemoteDBRepo();
    }

    public static RemoteDBRepo getInstance() {
        if (remoteDBRepo.connection == null)
            remoteDBRepo.connect().subscribeOn(Schedulers.io()).subscribe(() -> Log.i(TAG, "Database connection is configured properly."), (error) -> {
                Log.e(TAG, "Database connection is not configured properly.", error);
            });
        return remoteDBRepo;
    }

    private Completable connect() {
        return Completable.fromCallable(() -> {
            try {
                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                HashMap<String, String> configs = loadConfigs();
                String url =
                        String.format(
                                "jdbc:firebirdsql://%s/%s?encoding=ISO8859_1",
                                configs.get("server"),
                                configs.get("path"));
                Log.d(TAG, "Connection URL: $url");
                connection = DriverManager.getConnection(url, "SYSDBA", "masterkey");
                return true;
            } catch (SQLException | ClassNotFoundException error) {
                error.printStackTrace();
                return false;
            }
        });
    }

    public Completable reconnect() {
        return connect();
    }

    public Completable checkConnection() {
        return Completable.fromCallable(() -> {
            return connection != null;
//            throw new ExceptionInInitializerError("Database connection is not initialized!");
        });
    }

    public Connection getConnection() {
        return connection;
    }


    public void setPreferencesRepository(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }
}
