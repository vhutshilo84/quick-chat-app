# QuickChat — PROG5121 PoE Part 3 (Store Data and Display Task Report)

Part 3 builds on Parts 1 and 2. Registration, login, sending / storing /
disregarding messages and JSON persistence are unchanged. This part adds the
five required collections (arrays) and the **Stored Messages** report features.

## What the app does

1. Register a user (username, password, SA cell number) — Part 1 rules.
2. Log in with those details. **Messages can only be sent after a successful login.**
3. Shows `Welcome to QuickChat.` and asks how many messages the user wants to enter.
4. Shows the numeric menu until the user quits:
   - `1) Send Messages`
   - `2) Show recently sent messages` → displays `Coming Soon.`
   - `3) Stored Messages` → Part 3 sub-menu (see below)
   - `4) Quit`
5. Uses a **for loop** to capture exactly the requested number of messages.
6. For each message the user can **Send**, **Disregard** or **Store** (JSON).
7. Every action also populates the Part 3 collections (no hard-coding):
   - **Sent Messages**
   - **Disregarded Messages**
   - **Stored Messages** (also loaded from the JSON file)
   - **Message Hash**
   - **Message ID**

### Stored Messages sub-menu (option 3)

| Key | Feature |
|-----|---------|
| a | Display sender and recipient of all stored messages |
| b | Display the longest stored message |
| c | Search for a message ID → recipient + message |
| d | Search all messages for a particular recipient |
| e | Delete a message using the message hash |
| f | Display a full report (Message Hash, Recipient, Message) |
| 0 | Back to main menu |

## Project structure

```
QuickChat-Part3/
├── pom.xml
├── README.md
├── .gitignore
├── .github/workflows/testJava.yml
└── src
    ├── main/java/quickchat
    │   ├── Main.java              registration → login → menu → message loop + Part 3 menu
    │   ├── Login.java             Part 1 registration / login
    │   ├── ValidationUtils.java   shared validation (username, password, cell, length)
    │   ├── Message.java           Part 2 message model, hash, send/store/disregard
    │   ├── MessageRecord.java     POJO written to / read from JSON
    │   ├── MessageStore.java      Gson-based JSON read/write
    │   └── MessageManager.java    Part 3 arrays + search / delete / report
    └── test/java/quickchat
        ├── LoginTest.java
        ├── MessageTest.java
        ├── MessageStoreTest.java
        ├── MessageManagerTest.java   Part 3 unit tests (brief test data)
        └── QuickChatFlowTest.java    end-to-end console script
```

## Unit tests (Part 3)

`MessageManagerTest` uses the exact test data from the brief (Messages 1–5)
and asserts the expected responses:

- Sent Messages array contains "Did you get the cake?" and "It is dinner time !"
- Longest message is Message 2
- Search by message ID (Message 4) returns the message text
- Search by recipient +27838884567 returns Messages 2 and 5
- Delete by hash for Message 2 returns the required confirmation string
- Report contains Message Hash, Recipient and Message

All 47 tests pass (`mvn test`).

## How to run

```bash
mvn test
mvn -q exec:java
# or after packaging:
mvn package
java -jar target/QuickChat-Part3-3.0.jar
```

## GitHub Actions

`.github/workflows/testJava.yml` runs the JUnit suite on every push so the
code is tested with every change (CI requirement of the brief).

## Attribution

- Cell-number regex approach adapted from Baeldung (see `ValidationUtils`).
- JSON serialisation uses Google Gson (see `MessageStore`).
