DROP DATABASE IF EXISTS buscompany;
CREATE DATABASE buscompany;
USE buscompany;

-- CREATE USER 'test'@'localhost' identified by 'test';
-- GRANT ALL PRIVILEGES ON * . * TO 'test'@'localhost';
-- FLUSH PRIVILEGES;

CREATE TABLE user (

	id 			INT(11) 	NOT NULL AUTO_INCREMENT,
    firstname 	VARCHAR(50) NOT NULL,
    lastname 	VARCHAR(50) NOT NULL,
    patronymic 	VARCHAR(50),
    login 		VARCHAR(50) NOT NULL,
    password 	VARCHAR(50) NOT NULL,
    user_type 	ENUM('ADMIN', 'CLIENT') NOT NULL,
    deleted 	BOOL 	    NOT NULL,
    
    UNIQUE 	KEY(login),
    PRIMARY KEY (id)

) ENGINE=INNODB DEFAULT CHARSET=utf8;


CREATE TABLE admin (

	id_user  INT(11) 	 NOT NULL,
    position VARCHAR(50) NOT NULL,

	FOREIGN KEY(id_user) REFERENCES user(id) ON DELETE CASCADE
    
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE client(

	id_user INT(11) 	NOT NULL,
	phone 	VARCHAR(50) NOT NULL,
	email 	VARCHAR(50) NOT NULL,

	UNIQUE 	KEY(email),
	FOREIGN KEY(id_user) REFERENCES user(id) ON DELETE CASCADE
    
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE  cookie(

	id_user 			INT(11) 	NOT NULL,
    java_session_id 	VARCHAR(50) NOT NULL,
    time_leave 			DATETIME 	NOT NULL,
    
    PRIMARY KEY(java_session_id),
    UNIQUE 	KEY(id_user, java_session_id),
	FOREIGN KEY(id_user) REFERENCES user(id) ON DELETE CASCADE

) ENGINE=INNODB DEFAULT CHARSET=utf8;


CREATE TABLE bus (

	id			INT(11)		NOT NULL AUTO_INCREMENT,
	bus_name 	VARCHAR(50) NOT NULL,
	place_count INT(11) 	NOT NULL,
    
    PRIMARY KEY (id)

) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip (

	id 		 	 INT(11) 	 NOT NULL AUTO_INCREMENT,
    id_bus 	 	 INT(11)	 NOT NULL,
    from_station VARCHAR(50) NOT NULL,
    to_station 	 VARCHAR(50) NOT NULL,
    start 		 TIME 		 NOT NULL,
    duration 	 TIME 		 NOT NULL,
    price 		 DOUBLE 	 NOT NULL,
    approved 	 BOOL 		 NOT NULL,
    
	FOREIGN KEY(id_bus) REFERENCES bus(id) ON DELETE CASCADE,
    PRIMARY KEY(id)

) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE schedule (

	id_trip 	INT(11) 	NOT NULL,
    from_date 	DATE 		NOT NULL,
    to_date 	DATE 		NOT NULL,
    period 		VARCHAR(50) NOT NULL,
    
	FOREIGN KEY(id_trip) REFERENCES trip(id) ON DELETE CASCADE
    
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip_date (

	id			   INT(11) NOT NULL AUTO_INCREMENT,
    id_trip 	   INT(11) NOT NULL,
    trip_date 	   DATE	   NOT NULL,
    free_places    INT(11) NOT NULL,
    
    
    UNIQUE (id_trip, trip_date),
	FOREIGN KEY(id_trip) REFERENCES trip(id) ON DELETE CASCADE,
    PRIMARY KEY(id)

) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `order` (

	id 			 INT(11) NOT NULL AUTO_INCREMENT,
    id_trip_date INT(11) NOT NULL,
    id_client 	 INT(11) NOT NULL,
    total_price  INT(11) NOT NULL,
    
	FOREIGN KEY(id_trip_date) REFERENCES trip_date(id)   ON DELETE CASCADE,
	FOREIGN KEY(id_client)    REFERENCES client(id_user) ON DELETE CASCADE,
    PRIMARY KEY(id)

) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE passenger (

	id		  INT(11)	  NOT NULL AUTO_INCREMENT,
	id_order  INT(11) 	  NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname  VARCHAR(50) NOT NULL,
    passport  VARCHAR(50) NOT NULL,
	
    
    UNIQUE(passport, id_order),
    FOREIGN KEY(id_order) REFERENCES `order`(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
    
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE places(
	
	id_trip_date INT(11) NOT NULL,
	place 		 INT(11) NOT NULL,
	id_passenger INT(11),
    
    UNIQUE(id_trip_date, place),
    FOREIGN KEY(id_trip_date) REFERENCES trip_date(id) ON DELETE CASCADE,
    FOREIGN KEY(id_passenger) REFERENCES passenger(id) ON DELETE SET NULL


) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO bus(id, bus_name, place_count) VALUE(NULL, "Автобус", 30);
INSERT INTO bus(id, bus_name, place_count) VALUE(NULL, "Газель", 12);
INSERT INTO bus(id, bus_name, place_count) VALUE(NULL, "Микро автобус", 16);
INSERT INTO bus(id, bus_name, place_count) VALUE(NULL, "Двухэтажный автобус", 60);