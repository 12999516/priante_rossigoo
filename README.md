# RossiGo - Sistema di Noleggio Bici e Monopattini con Maven + SQLite

Progetto backend in Java per la gestione del noleggio di una flotta cittadina di monopattini e biciclette elettriche con persistenza su database SQLite e gestione robusta delle eccezioni custom.

---

## 1. Struttura del Progetto e Package

Il codice è organizzato seguendo la separazione delle responsabilità nei seguenti package:
- `org.example.modello`: definisce le entità di dominio (`Veicolo`, `Bici`, `Monopattino`, `Utente`, `Noleggio`, `StatoVeicolo`, `ModelloAccess`).
- `org.example.eccezioni`: gerarchia di eccezioni custom che estendono `RossiGoException` (`BatteriaScaricaException`, `CreditoInsufficienteException`, `EmailNonValidaException`, `ErroreDatabaseException`, `UtenteNonTrovatoException`, `VeicoloNonDisponibileException`, `CodiceFiscaleNonValidoException`).
- `org.example.servizio`: logica di business e gestione della flotta (`FlottaService`), operante polimorficamente sul tipo `Veicolo`.
- `org.example.persistenza`: accesso al database SQLite tramite design pattern Singleton e PreparedStatements (`DatabaseManager`, `VeicoloDAO`, `UtenteDAO`, `NoleggioDAO`).
- `org.example.app`: punto d'ingresso e simulazione interattiva (`Main`).

---

## 2. Librerie Esterne Utilizzate

Come da requisiti di progetto, sono state integrate e utilizzate le seguenti librerie esterne tramite Maven:

1. **SQLite JDBC (org.xerial:sqlite-jdbc)**  
   *Motivazione e utilizzo:* Driver JDBC necessario per connettersi al database SQLite `rossigo.db`. Viene utilizzato all'interno del package `persistenza` (`DatabaseManager`, `VeicoloDAO`, `UtenteDAO`, `NoleggioDAO`) per gestire la persistenza dei veicoli, degli utenti, dei crediti e dello storico noleggi.

2. **ASCII Art - JFiglet (com.github.lalyos:jfiglet)**  
   *Motivazione e utilizzo:* Consente di convertire stringhe di testo in banner tipografici ASCII art. Viene impiegata nella classe `Main` (`stampaBanner()`) all'avvio del programma per renderizzare a terminale il titolo banner "RossiGo".

3. **Apache Commons Validator (commons-validator:commons-validator)**  
   *Motivazione e utilizzo:* Fornisce utilità per la validazione formale di dati in ingresso, in particolare `EmailValidator`. Viene impiegata nel costruttore della classe `Utente` per validare l'indirizzo email al momento della registrazione e lanciare `EmailNonValidaException` in caso di formato non corretto.

---

## 3. Requisiti e Compilazione

- **Java**: versione 25
- **Maven**: 3.9+

### Compilazione del progetto
```bash
mvn clean compile
```

### Esecuzione della simulazione
```bash
mvn exec:java -Dexec.mainClass="org.example.app.Main"
```
oppure eseguendo direttamente la classe `org.example.app.Main` dal proprio IDE (IntelliJ IDEA).
