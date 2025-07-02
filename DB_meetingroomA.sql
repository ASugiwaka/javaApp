/* meetingroomA データベース作成　*/
DROP DATABASE IF EXISTS meetingroomA;
CREATE DATABASE meetingroomA;
use meetingroomA;

/*　利用者　*/
DROP TABLE IF EXISTS user;
/*
CREATE TABLE user(
id varchar(7) PRIMARY KEY
, password varchar(10) NOT NULL
, name varchar(10) 
, address varchar(30) 
, CHECK (CHAR_LENGTH(password) >= 6)
)CHARSET=utf8mb4 ENGINE=InnoDB;
*/

/* パスワード暗号化 */
CREATE TABLE user (
	id VARCHAR(7) PRIMARY KEY,
	password CHAR(64) NOT NULL,
	name VARCHAR(10),
	address VARCHAR(30)
);

/* Room */
DROP TABLE IF EXISTS room;
CREATE TABLE room(
id char(4) PRIMARY KEY
, name varchar(20)
)CHARSET=utf8mb4 ENGINE=InnoDB;

/* 予約 */
DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation(
id int AUTO_INCREMENT PRIMARY KEY
, roomId varchar(4) NOT NULL
, date Date NOT NULL
, start Time NOT NULL
, end Time NOT NULL
, userId varchar(7) NOT NULL
, UNIQUE KEY uniq_room_datetime (roomId, date, start)
, FOREIGN KEY (roomId) REFERENCES room(id)
, FOREIGN KEY (userId) REFERENCES user(id)
)CHARSET=utf8mb4 ENGINE=InnoDB;


/*------------------------------------------------*/
/*  mySQL ユーザと権限設定                         */
/*  meetingroomAをローカル'127.0.0.1'で使用するユーザ  */
/*------------------------------------------------*/

USE mysql;
/* 参照更新ユーザ　user */
DROP USER IF EXISTS 'user'@'127.0.0.1';
CREATE USER 'user'@'127.0.0.1' IDENTIFIED BY 'pass';
GRANT SELECT, INSERT, DELETE ON meetingroomA.* TO 'user'@'127.0.0.1';

/* 適用　*/
FLUSH PRIVILEGES;


/*------------------------------------------------*/
/* テストデータ                                   */
/*------------------------------------------------*/
use meetingrooma;

/*
INSERT INTO user VALUES
('2500001','xxxxxx','情報太郎','東京都')
,('2500015','yyyyyy','情報花子','大阪府');
UNLOCK TABLES;*/

/* パスワード暗号化 */
/* パスワードをSHA-256でハッシュ化してます */
INSERT INTO user VALUES ('1100003', SHA2('xxxxxx', 256), '情報太郎','東京都');
INSERT INTO user VALUES('1100015', SHA2('yyyyyy', 256),'情報花子','大阪府');
INSERT INTO user VALUES ('2500001', SHA2('xxxxxx', 256), '鈴木一朗','愛知県');
INSERT INTO user VALUES('2500015', SHA2('yyyyyy', 256),'田中道代','三重県');

INSERT INTO room VALUES
('0201','大会議室')
,('0301','３Ａ会議室')
,('0302','３Ｂ会議室');
UNLOCK TABLES;

INSERT INTO reservation VALUES
(NULL,'0201','2025-06-10','09:00:00','10:00:00','2500001')
,(NULL,'0201','2025-06-10','11:00:00','12:00:00','2500001')
,(NULL,'0201','2025-06-11','09:00:00','10:00:00','2500001')
,(NULL,'0301','2025-06-11','12:00:00','13:00:00','2500001')
,(NULL,'0301','2025-06-11','15:00:00','16:00:00','2500001')
,(NULL,'0302','2025-06-11','13:00:00','14:00:00','2500015');
UNLOCK TABLES;


/*------------------------------------------------*/
/* テストデータの確認                             */
/*------------------------------------------------*/
use meetingrooma;

SELECT * FROM user;
SELECT * FROM room;
SELECT * FROM reservation;

SELECT * FROM reservation, user, room WHERE reservation.roomid = room.id AND reservation.userid = user.id;

SELECT name, address FROM user WHERE id = '1100015' AND password = SHA2('yyyyyy', 256);
