CREATE TABLE product (
	maker VARCHAR(10) NOT NULL,
	model VARCHAR(50) PRIMARY KEY,
	type VARCHAR(10) NOT NULL CHECK (type IN ('PC','Laptop','Printer'))
);

CREATE TABLE pc (
	code INT PRIMARY KEY,
	model VARCHAR(50) NOT NULL REFERENCES product(model),
	speed INT NOT NULL,
	ram INT NOT NULL,
	hd FLOAT NOT NULL,
	cd VARCHAR(10) NOT NULL,
	price MONEY
);

CREATE TABLE laptop (
	code INT PRIMARY KEY,
	model VARCHAR(50) NOT NULL REFERENCES product(model),
	speed INT NOT NULL,
	ram INT NOT NULL,
	hd FLOAT NOT NULL,
	screen INT NOT NULL,
	price MONEY
);

CREATE TABLE printer (
	code INT PRIMARY KEY,
	model VARCHAR(50) NOT NULL REFERENCES product(model),
	color CHAR(1) NOT NULL CHECK (color IN ('y','n')),
	type VARCHAR(10) NOT NULL CHECK (type IN ('Laser','Jet','Matrix')),
	price MONEY
);
