--
-- File generated with SQLiteStudio v3.2.1 on za dec. 15 16:11:41 2018
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: card
DROP TABLE IF EXISTS card;
CREATE TABLE "card"
(
	idcard integer not null
		primary key,
	path text not null,
	theme text
);
INSERT INTO card (idcard, path, theme) VALUES (0, '../images/0_mario/0_0.png', 'mario_cover');
INSERT INTO card (idcard, path, theme) VALUES (1, '../images/0_mario/0_1.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (2, '../images/0_mario/0_2.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (3, '../images/0_mario/0_3.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (4, '../images/0_mario/0_4.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (5, '../images/0_mario/0_5.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (6, '../images/0_mario/0_6.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (7, '../images/0_mario/0_7.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (8, '../images/0_mario/0_8.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (9, '../images/0_mario/0_9.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (10, '../images/0_mario/0_10.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (11, '../images/0_mario/0_11.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (12, '../images/0_mario/0_12.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (13, '../images/0_mario/0_13.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (14, '../images/0_mario/0_14.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (15, '../images/0_mario/0_15.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (16, '../images/0_mario/0_16.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (17, '../images/0_mario/0_17.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (18, '../images/0_mario/0_18.png', 'mario');
INSERT INTO card (idcard, path, theme) VALUES (19, '../images/1_bh/1_0.png', 'bh_cover');
INSERT INTO card (idcard, path, theme) VALUES (20, '../images/1_bh/1_1.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (21, '../images/1_bh/1_2.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (22, '../images/1_bh/1_3.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (23, '../images/1_bh/1_4.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (24, '../images/1_bh/1_5.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (25, '../images/1_bh/1_6.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (26, '../images/1_bh/1_7.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (27, '../images/1_bh/1_8.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (28, '../images/1_bh/1_9.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (29, '../images/1_bh/1_10.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (30, '../images/1_bh/1_11.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (31, '../images/1_bh/1_12.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (32, '../images/1_bh/1_13.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (33, '../images/1_bh/1_14.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (34, '../images/1_bh/1_15.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (35, '../images/1_bh/1_16.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (36, '../images/1_bh/1_17.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (37, '../images/1_bh/1_18.png', 'bh');
INSERT INTO card (idcard, path, theme) VALUES (38, '../images/2_EMMA/2_0.png', 'EMMA_cover');
INSERT INTO card (idcard, path, theme) VALUES (39, '../images/2_EMMA/2_1.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (40, '../images/2_EMMA/2_2.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (41, '../images/2_EMMA/2_3.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (42, '../images/2_EMMA/2_4.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (43, '../images/2_EMMA/2_5.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (44, '../images/2_EMMA/2_6.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (45, '../images/2_EMMA/2_7.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (46, '../images/2_EMMA/2_8.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (47, '../images/2_EMMA/2_9.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (48, '../images/2_EMMA/2_10.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (49, '../images/2_EMMA/2_11.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (50, '../images/2_EMMA/2_12.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (51, '../images/2_EMMA/2_13.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (52, '../images/2_EMMA/2_14.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (53, '../images/2_EMMA/2_15.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (54, '../images/2_EMMA/2_16.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (55, '../images/2_EMMA/2_17.png', 'EMMA');
INSERT INTO card (idcard, path, theme) VALUES (56, '../images/2_EMMA/2_18.png', 'EMMA');

-- Table: cardgame
DROP TABLE IF EXISTS cardgame;
CREATE TABLE "cardgame" (
  "game_idgame" integer(11) NOT NULL,
  "card_idcard" integer(11) NOT NULL,
  "index" integer(11),
  "isTurned" integer(2) DEFAULT 0 COLLATE RTRIM,
  CONSTRAINT "fk_table2_card1" FOREIGN KEY ("card_idcard") REFERENCES "card" ("idcard") ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT "fk_table2_game2" FOREIGN KEY ("game_idgame") REFERENCES "game" ("idgame") ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- Table: game
DROP TABLE IF EXISTS game;
CREATE TABLE "game" (
  "idgame" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "owner" integer(11),
  "maxNumberOfPlayers" integer(11) NOT NULL,
  "curNumberOfPlayers" integer(11) NOT NULL,
  "size" integer(11),
  "createDate" integer(11) NOT NULL,
  CONSTRAINT "id" FOREIGN KEY ("owner") REFERENCES "player" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- Table: gameplayer
DROP TABLE IF EXISTS gameplayer;
CREATE TABLE "gameplayer" (
  "game_idgame" integer(11) NOT NULL,
  "player_id" integer(11) NOT NULL,
  "gameScore" integer(11) DEFAULT NULL,
  CONSTRAINT "fk_table2_game1" FOREIGN KEY ("game_idgame") REFERENCES "game" ("idgame") ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT "fk_table2_player1" FOREIGN KEY ("player_id") REFERENCES "player" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- Table: player
DROP TABLE IF EXISTS player;
CREATE TABLE "player" (
  "username" text(45) NOT NULL,
  "password" text(185) NOT NULL,
  "email" text(45) NOT NULL,
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "token" text(45),
  "totalScore" integer(11) DEFAULT NULL,
  "joinDate" integer(11) DEFAULT NULL,
  "lastGameDate" integer(11) DEFAULT NULL
);

-- Index: fk_table2_card1_idx
DROP INDEX IF EXISTS fk_table2_card1_idx;
CREATE INDEX "fk_table2_card1_idx"
ON "cardgame" (
  "card_idcard" ASC
);

-- Index: fk_table2_game1_idx
DROP INDEX IF EXISTS fk_table2_game1_idx;
CREATE INDEX "fk_table2_game1_idx"
ON "gameplayer" (
  "game_idgame" ASC
);

-- Index: fk_table2_game2_idx
DROP INDEX IF EXISTS fk_table2_game2_idx;
CREATE INDEX "fk_table2_game2_idx"
ON "cardgame" (
  "game_idgame" ASC
);

-- Index: fk_table2_player1_idx
DROP INDEX IF EXISTS fk_table2_player1_idx;
CREATE INDEX "fk_table2_player1_idx"
ON "gameplayer" (
  "player_id" ASC
);

-- Index: id_idx
DROP INDEX IF EXISTS id_idx;
CREATE INDEX "id_idx"
ON "game" (
  "owner" ASC
);

-- Index: id_UNIQUE
DROP INDEX IF EXISTS id_UNIQUE;
CREATE INDEX "id_UNIQUE"
ON "player" (
  "id" ASC
);

-- Index: username_UNIQUE
DROP INDEX IF EXISTS username_UNIQUE;
CREATE INDEX "username_UNIQUE"
ON "player" (
  "username" ASC
);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
