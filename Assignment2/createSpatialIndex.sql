CREATE TABLE IF NOT EXISTS item_coordPoint (
	item_id INT PRIMARY KEY,
	coords POINT NOT NULL
) ENGINE = MyISAM;

INSERT IGNORE INTO item_coordPoint(item_id,coords)
	SELECT item_id, POINT(latitude,longitude) 
	FROM item_coordinates;


CREATE spatial INDEX ix_spatial_mytable_pt ON item_coordPoint(coords);
 
