# csce247-escape-game

This escape room project is a game, "The Haunting of Hallow Manor", where users solve different puzzles such as word games, memory challenges, and mazes before time runs out to escape the house. You can make an account, keep track of your progress, and earn a certificate when you finish the game. The game has different difficulty settings, utilizes hints, penalties, and even saves your progress with checkpoints. A leaderboard is also a included so players can see how their scores and times compare, making it both fun and competitive.

Backend Implementation and Scenario Demonstration: https://drive.google.com/file/d/16OxFOW4oXl6AZ4f1UpfZ-p5SAlRrMJzy/view?usp=sharing


Test-Plan.pdf : details all test cases and is located in docs folder

Coverage-Report.pdf : comprehensive report showing how much of com.model is covered in tests (excludes consoleview, gameview, gameUI, getter/setters) located in docs folder

Yoshi: AuthenticationServiceTest, CertificateServiceTest, GameDataFacadeTest, GameProgressServiceTest, LeaderboardServiceTest, MatchingGameTest, MazeGameTest

Bugs: #233-#241 (working on resolving)

Test Creation: #232, #224, #222, #221, #217, #213, #206


Remy: GameDataLoaderTest, GameDataWriterTest, AuthControllerTest, CardTest, CertificateTest, ConsoleViewTest, GameControllerTest

Bugs: #243 (resolved), #244 (working on resolving)

Test Creation: #205, #207, #208, #209, #210, #211, #214, #215


Aashrith: GameDataTest, GameFactoryTest, PlayerTest, PuzzleTest, PuzzleGameTest, UserTest, WordPuzzleGameTest

Bugs:

Test Creation: #212, #216, #225, #227, #228, #229, #231


Nitin: MazeTest, HintTest, LeaderboardEntryTest, UserProgressTest, PositionTest,

Bugs: #232 (fixed)

Test Creation: #219, #220, #222, #223, #226, #230,

