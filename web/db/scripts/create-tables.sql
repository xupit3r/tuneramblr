-- Define the song table
-- TODO add user ID to this table (foreign key to users table)
CREATE TABLE song (
	id int not null AUTO_INCREMENT primary key,
	lat double,
	lng double,
	artist varchar(100),
	title varchar(100),
	album varchar(100),
	genre varchar(100));
	
-- Define weather table
-- TODO


-- Define userdef table
-- TODO


-- Define playlist table
-- TODO


-- Define users table
-- TODO