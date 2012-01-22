-- Define the song table
-- TODO add user ID to this table (foreign key to users table)
CREATE TABLE song (
	id int not null AUTO_INCREMENT,
	lat double,
	lng double,
	artist varchar(100),
	title varchar(100),
	album varchar(100),
	genre varchar(100),
	PRIMARY KEY (id)
);

-- NOTE: I think that the metadata and weather
--       tables would be better in a key/value
--       type DB.  this data will need to be
--       crunched and a DB like this would be 
--       more effective than a typical relational 
--       DB.
	
-- Define weather table
-- TODO add user ID to this table (foreign key to users table)
CREATE TABLE weather (
	id int not null AUTO_INCREMENT,
	song_id int not null,
	weather_txt varchar(500),
	PRIMARY KEY (id),
	FOREIGN KEY (song_id) references song(id)
);


-- Define userdef table
-- TODO add user ID to this table (foreign key to users table)
CREATE TABLE userdef (
	id int not null AUTO_INCREMENT,
	song_id int not null,
	property_txt varchar(500),
	PRIMARY KEY (id),
	FOREIGN KEY (song_id) references song(id)
);


-- Define playlist table
-- TODO


-- Define users table
-- TODO