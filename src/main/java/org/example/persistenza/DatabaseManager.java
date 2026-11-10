package org.example.persistenza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager databaseManager = null;
    private static final String URL = "jdbc:sqlite:rossigo.db";

    private DatabaseManager(){
        initDB();
    }

    public static synchronized DatabaseManager getInstance(){
        if (databaseManager == null) {
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try(Statement stmt = connection.createStatement()){
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }

    private void initDB(){
        String sql = """
                CREATE TABLE IF NOT EXISTS utenti (
                    id TEXT PRIMARY KEY,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL,
                    codice_fiscale TEXT UNIQUE NOT NULL,
                    credito REAL NOT NULL
                );
                
                CREATE TABLE IF NOT EXISTS veicoli (
                    codice TEXT PRIMARY KEY,
                    tipo TEXT NOT NULL,
                    batteria REAL NOT NULL,
                    stato TEXT NOT NULL,
                    numero_marce INTEGER,
                    limitato_pedonale INTEGER
                );
                
                CREATE TABLE IF NOT EXISTS noleggi (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    utente_id TEXT NOT NULL,
                    veicolo_codice TEXT NOT NULL,
                    orario_inizio TEXT NOT NULL,
                    orario_fine TEXT,
                    FOREIGN KEY (utente_id) REFERENCES utenti(id),
                    FOREIGN KEY (veicolo_codice) REFERENCES veicoli(codice)
                );
                """;

        try(Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            for (String query : sql.split(";")){
                if (!query.trim().isEmpty()){
                    statement.execute(query);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
