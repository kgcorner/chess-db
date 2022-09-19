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
