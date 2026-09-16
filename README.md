# quick chat app 
part1

# QuickChat — PROG5121 PoE Part 1 (Registration & Login)


## What's included
- `src/main/java/quickchat/Login.java` — registration & login validation logic
  (`checkUserName`, `checkPasswordComplexity`, `checkCellPhoneNumber`,
  `registerUser`, `loginUser`, `returnLoginStatus`).
- `src/main/java/quickchat/Main.java` — console app tying it together (no GUI).
- `src/test/java/quickchat/LoginTest.java` — JUnit 5 tests using the exact
  test data from the brief.
- `.github/workflows/testJava.yml` — GitHub Actions workflow that runs the
  tests automatically on every push.
- `pom.xml` — Maven project file (JUnit 5 dependency, Java 17).


## Opening this in NetBeans
1. Open NetBeans → **File → Open Project** → select this folder
   (`QuickChat-Part1`). NetBeans recognises the `pom.xml` and imports it as
   a Maven project automatically.
2. Right-click the project → **Clean and Build** to compile.
3. Run `Main.java` (right-click → Run File) to try the console app.
4. Run the tests: right-click the project → **Test** (or right-click
   `LoginTest.java` → Test File).


## Pushing to GitHub (minimum 6 commits required)
```
git init
git add .
git commit -m "Initial project structure with pom.xml"
git commit -m "Add Login class with username/password checks"
git commit -m "Add cell phone number validation"
git commit -m "Add Main console driver"
git commit -m "Add JUnit tests for Login"
git commit -m "Add GitHub Actions workflow"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```
Make small, separate commits as you build each piece rather than one big
commit at the end — the rubric checks for real commit history, not just a
final push.


## Still to do before submitting Part 1
- Push this to your own GitHub repo and submit the link on Arc.
- Record your unlisted YouTube walkthrough (your own voice, no AI voice)
  explaining the code, the logic/flow, and showing the app running and the
  tests passing.
- Fill in the assessment cover sheet with your name and student number.
- Confirm the GitHub Actions run is green before submitting.

