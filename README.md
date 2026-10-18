# QuickChat — PROG5121 PoE Part 2 (Sending Messages)

Part 2 builds on the Part 1 registration and login feature. The Part 1 code is
kept and improved (validation rules now live in one place so the cell number
check can be reused), and the messaging features from Part 2 are added.

## What the app does

1. Register a user (username, password, SA cell number) — Part 1 rules.
2. Log in with those details. **Messages can only be sent after a successful
   login.**
3. Shows `Welcome to QuickChat.` and asks how many messages the user wants to
   enter.
4. Shows the numeric menu until the user quits:
   - `1) Send Messages`
   - `2) Show recently sent messages` → displays `Coming Soon.`
   - `3) Quit`
5. Uses a **for loop** to capture exactly the requested number of messages, and
   the loop counter is used as the message number.
6. For each message it captures a recipient cell number and the message text,
   validates both, generates a message ID and hash, then asks:
   - `1) Send Message` → `Message successfully sent.`
   - `2) Disregard Message` → `Press 0 to delete the message.`
   - `3) Store Message to send later` → `Message successfully stored.`
7. Displays every message's details in the required order
   (Message ID, Message Hash, Recipient, Message) and prints the total number
   of messages sent once the batch is finished.
8. Stored messages are written to `messages.json`.

## Project structure

```
QuickChat-Part2/
├── pom.xml
├── README.md
├── .gitignore
├── .github/workflows/testJava.yml
└── src
    ├── main/java/quickchat
    │   ├── Main.java             console app: registration → login → menu → message loop
    │   ├── Login.java            Part 1 registration + login (now uses ValidationUtils)
    │   ├── ValidationUtils.java  shared username / password / cell number / length rules
    │   ├── Message.java          message model, ID, hash, send / store / disregard
    │   ├── MessageStore.java     reads and writes messages.json (Gson)
    │   └── MessageRecord.java    the JSON shape of one stored message
    └── test/java/quickchat
        ├── LoginTest.java        Part 1 tests (kept)
        ├── MessageTest.java      Part 2 tests (length, recipient, hash, ID, send/store)
        ├── MessageStoreTest.java JSON persistence tests
        └── QuickChatFlowTest.java end-to-end console flow test
```

## Part 2 methods on `Message`

| Method | What it does |
| --- | --- |
| `boolean checkMessageID()` | ID must not be longer than ten characters |
| `String checkRecipientCell()` | recipient needs an international code and a valid length |
| `String checkMessageLength()` | 250 character limit with the exact feedback messages |
| `String createMessageHash()` | `first two ID digits:message number:FIRSTwordLASTword` |
| `String SentMessage(int choice)` | send / disregard / store handling and messages |
| `String printMessages()` | all messages sent while the program has been running |
| `int returnTotalMessagess()` | total number of messages sent |
| `String storeMessage()` | writes the message to `messages.json` |

Example hash: message ID `0012345678`, message number `0`, message
`"Hi Mike, can you join us for dinner tonight?"` → **`00:0:HITONIGHT`**.

## How to run

**NetBeans** (same as Part 1): *File → Open Project* → select `QuickChat-Part2`,
then right-click `Main.java` → *Run File*. Run the tests with
right-click the project → *Test*.

**Command line with Maven** (Maven 3.8+ and JDK 17+):

```bash
mvn clean test          # compile and run all unit tests
mvn -q exec:java        # run the console app
mvn package             # builds target/QuickChat-Part2-2.0.jar
java -jar target/QuickChat-Part2-2.0.jar
```

The first Maven build downloads JUnit 5 and Gson, so it needs an internet
connection once.

### Sample session

```
=== QuickChat Registration ===
Enter a username (must contain '_' and be no more than 5 characters): kyl_1
...
Username successfully captured. Password successfully captured. Cell number successfully captured.

=== QuickChat Login ===
Enter your username: kyl_1
Enter your password: Ch&&sec@ke99!
Welcome Kyle, Smith it is great to see you again.
Welcome to QuickChat.
How many messages do you wish to send? 2
...
--- Message 1 of 2 ---
Enter the recipient's cell number (e.g. +27718693002): +27718693002
Cell phone number successfully captured.
Enter the message (maximum 250 characters): Hi Mike, can you join us for dinner tonight?
Message ready to send.
Message ID generated: 0053797772
Message ID: 0053797772
Message Hash: 00:0:HITONIGHT
Recipient: +27718693002
Message: Hi Mike, can you join us for dinner tonight?
...
Message successfully sent.
Total messages sent: 1
```

## Test data used (from the brief)

| Test | Data | Expected |
| --- | --- | --- |
| Message length OK | `"Hi Mike, can you join us for dinner tonight?"` | `Message ready to send.` |
| Message length too long | 260 characters | `Message exceeds 250 characters by 10; please reduce the size.` |
| Recipient OK | `+27718693002` | `Cell phone number successfully captured.` |
| Recipient invalid | `08575975889` | `Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.` |
| Message hash | ID `0012345678`, number `0`, first message | `00:0:HITONIGHT` |
| Message ID | generated ID | `Message ID generated: <ID>` |
| Send | option 1 | `Message successfully sent.` |
| Disregard | option 2 | `Press 0 to delete the message.` |
| Store | option 3 | `Message successfully stored.` |

The hash test data is also checked with a loop over several messages
(`MessageTest.testMessageHashesInLoop`), as the brief requires.

## How the Part 2 rubric is covered

| Rubric item | Where |
| --- | --- |
| Welcome message and numeric menu | `Main.WELCOME_MESSAGE`, `Main.printMenu()` |
| While loop that quits on input | `Main.runMenu()` |
| For loop for the assigned number of messages | `Main.sendMessages()` |
| Enter message details / variables stored | `Main.readRecipient()`, `Main.readMessageText()`, `Message` fields |
| Message number from the loop counter | `new Message(messageNumber, ...)` in `sendMessages()` |
| Message ID (random ten digits, substring used) | `Message.generateMessageID()`, `createMessageHash()` |
| Message hash (string manipulation + loop counter) | `Message.createMessageHash()` |
| Message details displayed in order | `Message.getMessageDetails()` |
| JSON file storage | `MessageStore` + `Message.storeMessage()` |
| Unit tests | `src/test/java/quickchat/*` (38 tests) |
| Automated tests | `.github/workflows/testJava.yml` |

## JSON storage

`messages.json` is created in the folder the app is started from (the project
folder in NetBeans). It is a JSON array so messages stored over several runs are
kept:

```json
[
  {
    "messageID": "0025899340",
    "messageNumber": 1,
    "messageHash": "00:1:HIPAYMENT",
    "recipient": "+27838884567",
    "message": "Hi Keegan, did you receive the payment?",
    "status": "Stored",
    "timestamp": "2026-10-17T15:31:29"
  }
]
```

JSON handling uses Google Gson, which escapes quotes/newlines correctly
(`MessageStore`). Unit tests write to a temporary folder, so running the tests
never touches the project's `messages.json`.

## References and attribution

- The Independent Institute of Education (2026) *PROG5121 Programming 1A:
  Portfolio of Evidence*. Assessment brief.
- Baeldung (2024) *Validate Phone Numbers With Java Regex*.
  https://www.baeldung.com/java-regex-validate-phone-numbers (accessed 2026).
  Used as the basis for the international cell number regex in
  `ValidationUtils` (adapted for the ten character rule).
- Google (2024) *Gson — A Java serialization/deserialization library to convert
  Java Objects into JSON and back*. https://github.com/google/gson
  (accessed 2026). Used for JSON storage.
- QuickBlox (2024) *Beginner's guide to chat app architecture*.
  https://quickblox.com/blog/beginners-guide-to-chat-app-architecture/
  (suggested by the brief).

## Pushing this to GitHub (minimum six commits)

```bash
git init
git add .
git commit -m "Keep Part 1 registration and login, share validation rules"
git commit -m "Add Message model with ID, hash and length checks"
git commit -m "Add JSON message storage with Gson"
git commit -m "Add welcome message and numeric menu with quit option"
git commit -m "Add for loop to capture the requested number of messages"
git commit -m "Add Part 2 unit tests and update CI workflow"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```

Do real, small commits while you build — the rubric checks history, not one big
push at the end.

## Still to do before submitting Part 2

- Push to your own GitHub repo on a feature branch (e.g. `KhanbanTasks`) and
  submit the link on Arc.
- Record the unlisted YouTube walkthrough (your own voice, no AI voice):
  explain the classes, the login gate, the menu/while loop, the message for
  loop, the ID/hash logic, JSON storage, and show the tests passing.
- Fill in the assessment cover sheet with your name and student number.
- Confirm the GitHub Actions run is green.

## Next (Part 3 preview)

Part 3 adds the arrays of sent / disregarded / stored messages, a fourth
"Stored Messages" menu option, and the reporting features. `MessageStore.readAll()`
already reads the JSON file back, ready for that step.
