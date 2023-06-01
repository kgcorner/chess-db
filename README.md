
[![codecov](https://codecov.io/gh/kgcorner/chess-db/branch/master/graph/badge.svg?token=dkRQqI9DQQ)](https://codecov.io/gh/kgcorner/chess-db)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/kgcorner/chess-db/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/kgcorner/chess-db/tree/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
# chess-db
Chess-db is a small database for conveniently storing and fetching chess games and positions. You can use this in your chess app to store games.
The database is build using Spring boot. You can easily integrate it with a spring boot service.

# Features
Currently below features are supported
* Storing chess Game
* Fetching games by FEN
* Fetching moves and how many time it was played in certain position using FEN.

# How to use
The db is made using Spring boot. Once will have to call ChessDbDriver.init() before using this db.
The DB itself doesn't has any data. It's just a program and the user will have to add games

# How to store games
Use [ByteWisePGNProcessor](https://github.com/kgcorner/chess-db/blob/master/chess-db/src/main/java/com/scriptchess/services/parsers/ByteWisePGNProcessor.java) to parse any given game in PGN format. And save the game using chessService.saveGame()
saveGame method will save the game as well as the position and moves on the given position. If the position is reached in previous games then moves will be merged
location for saving game files are define using property "games.path" and "fens.path" for position storage in application.properties file
  

# Unit Tests results
![code-coverage](https://codecov.io/gh/kgcorner/chess-db/branch/master/graphs/sunburst.svg?token=dkRQqI9DQQ)
                                                 

Taken from [codecov](https://about.codecov.io/).

The inner-most circle is the entire project, moving away from the center are folders then, finally, a single file. The size and color of each slice is representing the number of statements and the coverage, respectively.

# Code analysis
Code analysis is done using [Sonarcloud](https://sonarcloud.io/)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=bugs)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)