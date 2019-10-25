DROP TABLE IF EXISTS game, coefficient CASCADE;

CREATE TABLE game (
    id             SERIAL NOT NULL PRIMARY KEY,
    country        VARCHAR(50) NOT NULL,
    leagueName     VARCHAR(50) NOT NULL,
    season         VARCHAR(50) NOT NULL,
    gameDate       TIMESTAMP NOT NULL,
    firstCommand   VARCHAR(50) NOT NULL,
    secondCommand  VARCHAR(50) NOT NULL,
    firstBalls     INT NOT NULL,
    secondBalls    INT NOT NULL,
    coefHref       VARCHAR(50),
    coefficient_id INT UNIQUE NOT NULL
);

CREATE TABLE coefficient (
    id        SERIAL NOT NULL PRIMARY KEY,
    game_id   INT REFERENCES game(id) ON DELETE CASCADE,
    bookmaker VARCHAR(50),
    firstWin  VARCHAR(50) DEFAULT '-',
    tie       VARCHAR(50) DEFAULT '-',
    secondWin VARCHAR(50) DEFAULT '-',
    max1x2    VARCHAR(50) DEFAULT '-',
    min1x2    VARCHAR(50) DEFAULT '-',
    dch1X     VARCHAR(50) DEFAULT '-',
    dchx2     VARCHAR(50) DEFAULT '-'
);
