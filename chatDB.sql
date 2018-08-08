DROP DATABASE IF EXISTS activechat;
CREATE DATABASE activechat;

USE activechat;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  username          VARCHAR(30)     NOT NULL,
  password          VARCHAR(60)     NOT NULL,
  email             VARCHAR(50)     NOT NULL,
  role              VARCHAR(10)     NOT NULL,
  PRIMARY KEY (username)
) ENGINE = innodb DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS games;
CREATE TABLE games (
  idGame            INTEGER(30)     NOT NULL AUTO_INCREMENT,
  white             VARCHAR(30)     NOT NULL,
  black             VARCHAR(30)     NOT NULL,
--   currentTurn: 0-black turn, 1-white turn
  currentTurn       INTEGER(1)      NOT NULL,
  startDate         DATETIME        NOT NULL,
  boardState        VARCHAR(32)     NOT NULL,
  PRIMARY KEY (idGame)
) ENGINE = innodb DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS conversations;
CREATE TABLE conversations (
  idConversation    INTEGER(30)     NOT NULL AUTO_INCREMENT,
  idGame            INTEGER(30),
  PRIMARY KEY (idConversation),
  FOREIGN KEY (idGame) REFERENCES games(idGame)
) ENGINE = innodb DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS participation;
CREATE TABLE participation (
  idParticipation   INTEGER(30)     NOT NULL AUTO_INCREMENT,
  idConversation    INTEGER(30)     NOT NULL,
  participant       VARCHAR(30)     NOT NULL,
  PRIMARY KEY (idParticipation),
  FOREIGN KEY (idConversation) REFERENCES conversations(idConversation),
  FOREIGN KEY (participant) REFERENCES users(username)
) ENGINE = innodb DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS messages;
CREATE TABLE messages (
  idMessage         INTEGER(30)     NOT NULL AUTO_INCREMENT,
  idConversation    INTEGER(30)     NOT NULL,
  author            VARCHAR(30)     NOT NULL,
  content           TEXT            NOT NULL,
  sendingDate       DATETIME        NOT NULL,
  PRIMARY KEY (idMessage),
  FOREIGN KEY (idConversation) REFERENCES conversations(idConversation)
) ENGINE = innodb DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS archive;
CREATE TABLE archive (
  idGame            INTEGER(30)     NOT NULL AUTO_INCREMENT,
  idConversation    INTEGER(30)     NOT NULL,
  white             VARCHAR(30)     NOT NULL,
  black             VARCHAR(30)     NOT NULL,
--   result: 0-black win, 1-white win, 2-draw
  result            INTEGER(1)      NOT NULL,
  startDate         DATETIME        NOT NULL,
  endDate           DATETIME        NOT NULL,
  PRIMARY KEY (idGame),
  FOREIGN KEY (idConversation) REFERENCES conversations(idConversation)
) ENGINE = innodb DEFAULT CHARSET = utf8;


INSERT INTO activechat.users (username, password, email, role) VALUES
('user', '$2a$10$phG8Ykz8LTyIl05cQqewCeUXQltQIkBf1Rw6bJoLM9dfTmY9Q1GEy', 'user@wp.pl', 'USER'),
('user1', '$2a$10$eCb2i66xeUIEHoO617/ca.XlDHkrnkpWF4hh29VR0mBqVbiX97WG6', 'user1@wp.pl', 'USER'),
('user3', '$2a$10$xUYq5Qovzb8VP2wReq5mc.GDpYj7SpOJh3beW2.vW40.hDZZ44rh2', 'user3@wp.pl', 'USER'),
('user4', '$2a$10$KhOhZVm9CA/2OkYo53t9EeiNC2yQ3kanFoPhjQBq06Y1y4v2pGit6', 'user4@wp.pl', 'USER'),
('user5', '$2a$10$uALLml.t0xgXMDBt9cAs/O7ydzaOALPxf2Lkhc/SZT04u40PXLQOC', 'user5@wp.pl', 'USER'),
('user6', '$2a$10$4kw4HItRZozKQJsienuxu.XY7capE7eXdjvxWwusExiiRA/LxzCEi', 'user6@gmail.com', 'USER'),
('user7', '$2a$10$qIkJ6m14W.ExS4sIZAws8OiB645MGu8D6jjXoV9SuJe3i1i9R/vIi', 'user7@gmail.com', 'USER');

INSERT INTO activechat.games (idGame, white, black, currentTurn, startDate, boardState) VALUES
('1', 'user', 'user1', '1', '2018-03-30 12:10:00', 'bbbbbbbbbbbb--------wwwwwwwwwwww'),
('2', 'user3', 'user', '1', '2018-03-30 12:20:00', 'bbbbbbbbbbbb--------wwwwwwwwwwww'),
('3', 'user', 'user5', '1', '2018-03-30 12:30:00', 'bbbbbbbbbbbb--------wwwwwwwwwwww'),
('4', 'user', 'user7', '1', '2018-03-30 12:30:00', 'bbbbbbbbbbbb--------wwwwwwwwwwww');

INSERT INTO activechat.conversations (idConversation, idGame) VALUES
('1', '1'),
('2', '2'),
('3', '3'),
('4', '4');

INSERT INTO activechat.participation (idParticipation, idConversation, participant) VALUES
('1', '1', 'user'),
('2', '1', 'user1'),
('3', '2', 'user'),
('4', '2', 'user3'),
('5', '3', 'user'),
('6', '3', 'user5'),
('7', '4', 'user'),
('8', '4', 'user7');

INSERT INTO activechat.messages (idConversation, author, content, sendingDate) VALUES
('1', 'user', 'wiadomosc 1', '2018-03-30 12:00:00'),
('1', 'user', 'wiadomosc 2', '2018-03-30 12:01:00'),
('1', 'user1', 'wiadomosc 3', '2018-03-30 12:02:00'),
('1', 'user1', 'wiadomosc 4', '2018-03-30 12:03:00'),
('1', 'user', 'wiadomosc 5', '2018-03-30 12:04:00'),
('2', 'user', 'wiadomosc 6', '2018-03-30 12:05:00'),
('2', 'user3', 'wiadomosc 7', '2018-03-30 12:06:00'),
('3', 'user', 'wiadomosc 8', '2018-03-30 12:07:00'),
('4', 'user', 'wiadomosc 9', '2018-03-30 12:08:00'),
('4', 'user7', 'wiadomosc 10', '2018-03-30 12:09:00');

INSERT INTO activechat.archive (idGame, idConversation, white, black, result, startDate, endDate) VALUES
('1', '1', 'user', 'user1', '1', '2018-03-30 12:00:00', '2018-03-30 12:10:00'),
('2', '1', 'user1', 'user', '0', '2018-03-30 12:10:00', '2018-03-30 12:20:00'),
('3', '1', 'user', 'user1', '2', '2018-03-30 12:20:00', '2018-03-30 12:30:00'),
('4', '1', 'user', 'user1', '1', '2018-03-30 12:30:00', '2018-03-30 12:40:00'),
('5', '1', 'user', 'user1', '1', '2018-03-30 12:40:00', '2018-03-30 12:50:00'),
('6', '1', 'user', 'user1', '0', '2018-03-30 12:50:00', '2018-03-30 13:00:00'),
('7', '1', 'user', 'user1', '0', '2018-03-30 13:00:00', '2018-03-30 13:10:00'),
('8', '1', 'user1', 'user', '0', '2018-03-30 13:10:00', '2018-03-30 13:20:00'),
('9', '1', 'user1', 'user', '1', '2018-03-30 13:20:00', '2018-03-30 13:30:00');
