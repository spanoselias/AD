 

CREATE TABLE IF NOT EXISTS item_coordPoint (

	itemID INT PRIMARY KEY,
	coords POINT NOT NULL
) ENGINE = MyISAM;

INSERT IGNORE INTO item_coordPoint(itemID,coords)
	SELECT item_id, POINT(latitude,longitude) 
	FROM item_coordinates;


create spatial 
index ix_spatial_mytable_pt ON item_coordPoint(coords);

