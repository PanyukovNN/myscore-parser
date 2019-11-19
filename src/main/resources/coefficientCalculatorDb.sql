DROP TABLE IF EXISTS game, coefficient CASCADE;

CREATE TABLE country (
    id             SERIAL NOT NULL PRIMARY KEY,
    country_name   VARCHAR(50) NOT NULL
);

CREATE TABLE league (
    id                  SERIAL NOT NULL PRIMARY KEY,
    league_name         VARCHAR(100) NOT NULL,
    country_id          INT REFERENCES country(id) ON DELETE CASCADE,
    avg_home_goal       DECIMAL,
    avg_away_goal       DECIMAL,
    current_season_id   INT,
    previous_season_id  INT
);

CREATE TABLE team (
    id              SERIAL NOT NULL PRIMARY KEY,
    league_id       INT REFERENCES league(id) ON DELETE CASCADE,
    name            VARCHAR(100),
    avg_home_goal   DECIMAL,
    avg_away_goal   DECIMAL,
    avg_home_GC     DECIMAL,
    avg_away_GC     DECIMAL
);

CREATE TABLE match (
    id              SERIAL NOT NULL PRIMARY KEY,
    date            TIMESTAMP NOT NULL,
    league_id       INT REFERENCES league(id) ON DELETE CASCADE,
    season_id       INT REFERENCES season(id) ON DELETE CASCADE,
    home_team_id    INT REFERENCES team(id) ON DELETE CASCADE,
    away_team_id    INT REFERENCES team(id) ON DELETE CASCADE,
    home_goal       INT NOT NULL,
    away_goal       INT NOT NULL,
    first_win       INT,
    tie             INT,
    second_win      INT
);

CREATE TABLE season (
    id          SERIAL NOT NULL PRIMARY KEY,
    year_from   INT NOT NULL,
    year_to     INT NOt NULL,
    league_id   INT REFERENCES league(id) ON DELETE CASCADE
);
