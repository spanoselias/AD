CREATE DATABASE IF NOT EXISTS EBY;
USE EBY;

CREATE TABLE Category
(
	categoryID INT NOT NULL,	 
	name VARCHAR(256) NOT NULL,
	PRIMARY KEY (categoryID)
);

CREATE TABLE Country
( 	 
	countryID INT NOT NULL,	 
	name VARCHAR(256) NOT NULL,
	PRIMARY KEY (countryID)		
);

CREATE TABLE Seller
(	sellerID VARCHAR(256) NOT NULL,	
	rating DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (sellerID)	 
);

CREATE TABLE Bidder
(
	bidderID VARCHAR(256) NOT NULL,	
	rating DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (bidderID)	
);

CREATE TABLE Location
(
	locID INT NOT NULL,	
	location VARCHAR(256) NOT NULL,
	PRIMARY KEY (locID) 
);

CREATE TABLE Location_Det
(
	
	latitude DECIMAL(12,10) NOT NULL,
	longitude DECIMAL(12,10) NOT NULL,
	locID INT NOT NULL,
	PRIMARY KEY (latitude,longitude,locID),	 
	FOREIGN KEY (locID) REFERENCES Location(locID)					 
);


CREATE TABLE Item
(
	itemID INT NOT NULL,
	itemName VARCHAR(256) NOT NULL,
	firstBid DECIMAL(8,2) NOT NULL,	
	currently DECIMAL(8,2) NOT NULL,	
	started TIMESTAMP NOT NULL,
	ends TIMESTAMP NOT NULL,
	descr  VARCHAR(4000) NOT NULL,	
	noOfBids INT NOT NULL,	
	selID1 VARCHAR(256) NOT NULL,
	countryID1 INT NOT NULL,
	locID1 INT NOT NULL,
	PRIMARY KEY (itemID),
	FOREIGN KEY (selID1) REFERENCES Seller(sellerID),
	FOREIGN KEY (countryID1) REFERENCES Country(countryID),
	FOREIGN KEY (locID1) REFERENCES Location(locID)
);

CREATE TABLE Init_Price
(
	itemID INT NOT NULL,		 
	buyPrice DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (itemID),
	FOREIGN KEY (itemID) REFERENCES Item(itemID)	
);

CREATE TABLE Item_Category
(
	itemID INT NOT NULL,
	categoryID INT NOT NULL,	 
	PRIMARY KEY (itemID,categoryID),  
	FOREIGN KEY (itemID) REFERENCES Item(itemID),
	FOREIGN KEY (categoryID) REFERENCES Category(categoryID)		
);


CREATE TABLE Bid_Act
(
	itemID INT NOT NULL,
	bidderID VARCHAR(256) NOT NULL,
	time TIMESTAMP NOT NULL,
	amount DECIMAL(8,2) NOT NULL,
	PRIMARY KEY (itemID,bidderID,time),	 
	FOREIGN KEY (itemID) REFERENCES Item(itemID),
	FOREIGN KEY (bidderID) REFERENCES Bidder(bidderID)		
);

CREATE TABLE Bidder_Location
(
	bidderID VARCHAR(256) NOT NULL,	
	locID INT NOT NULL,
	PRIMARY KEY (bidderID),	 
	FOREIGN KEY (bidderID) REFERENCES Bidder(bidderID),
	FOREIGN KEY (locID) REFERENCES Location(locID)				 
);

CREATE TABLE Bidder_Country
(
	bidderID VARCHAR(256) NOT NULL,	
	countryID INT NOT NULL,	
	PRIMARY KEY (bidderID), 
	FOREIGN KEY (countryID) REFERENCES Country(countryID),
	FOREIGN KEY (bidderID) REFERENCES Bidder(bidderID)						 
);
