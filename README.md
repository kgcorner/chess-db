
[![codecov](https://codecov.io/gh/kgcorner/chess-db/branch/master/graph/badge.svg?token=dkRQqI9DQQ)](https://codecov.io/gh/kgcorner/chess-db)
[![Build Status](https://app.travis-ci.com/kgcorner/chess-db.svg?branch=master)](https://app.travis-ci.com/kgcorner/chess-db)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kgcorner_chess-db&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kgcorner_chess-db)
# chess-db
Chess-db is a small database for conveniently storing and fetching chess games. You can use this in your chess app to store games

#Features
Currently below features are supported
* Storing chess Game
* Storing Players info
* Saving tournament's Info
* Fetching games played with certain move sequence. eg You can fetch all the games that starts with these moves 1. e4 c6 2. Nf3 d5 3. e5 Bg4 4. d4 e6 5. h3 Bh5 6. g4 Bg6
* Fetching moves and how many time it was played after a certain move sequence. For example you can get what are the moves played after these moves 1. e4 c6 2. Nf3 d5 3. e5 Bg4 4. d4 e6 5. h3 Bh5 6. g4 Bg6
* Fetching all games played by player (identified by FIDE-id)
* Fetching all games played in a tournament (Identified by name and year)
* You can perform CRUD Operation on player
* You can fetch list of available tournaments or list of tournaments played in certain year


#How to use
The db is made using Spring boot. Once will have to call ChessDbDriver.init() before using this db.
The DB itself doesn't has any data. It';s just a program and the user will have to add games

#How to store games
In order to store games, the DB accepts game in PGN format. currently PGNs from below websites are supported
* [chessarena.com](https://chessarena.com/)
* [chess.com](https://chess.com)
* [lichess.org](https://lichess.org)

In order to fetch games after certain move eg:1. e4 c6 2. Nf3 d5 3. e5 Bg4 4. d4 e6 5. h3 Bh5 6. g4 Bg6
DB will accept the move string in below format
e4>c6>Nf3>d5>e5>Bg4>d4>e6>h3>Bh5>g4>Bg6

The same format of move string will be required for fetching next move after certain move sequence. DB will return a map that contains move notation (as key) and number of times it has been played(as value)


#Features yet to be developed
Below features are yet to be implemented
* Get game or games from certain position (This will require to fetch games using [FEN](https://www.chess.com/terms/fen-chess). [FEN](https://www.chess.com/terms/fen-chess) is not supported yet)
* Get game or game using an [ECO](https://www.365chess.com/eco.php)      

#Unit Tests results
![code-coverage](https://codecov.io/gh/kgcorner/chess-db/branch/master/graphs/sunburst.svg?token=dkRQqI9DQQ)
                                                 

Taken from [codecov](https://about.codecov.io/).

The inner-most circle is the entire project, moving away from the center are folders then, finally, a single file. The size and color of each slice is representing the number of statements and the coverage, respectively.

#Code analysis
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