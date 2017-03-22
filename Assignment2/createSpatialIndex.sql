CREATE TABLE IF NOT EXISTS item_coordPoint (
	item_id INT PRIMARY KEY,
	coords POINT NOT NULL
) ENGINE = MyISAM;

INSERT IGNORE INTO item_coordPoint(item_id,coords)
	SELECT item_id, POINT(latitude,longitude) 
	FROM item_coordinates;


SELECT IF (
    EXISTS(
        SELECT DISTINCT INDEX_NAME  FROM information_schema.statistics
        WHERE table_schema = 'ad'
        AND table_name = 'item_coordPoint' AND INDEX_NAME  like 'sPointIndex'
    )
    ,'SELECT ''index already exists'' Warning;'
    ,'CREATE SPATIAL INDEX sPointIndex on item_coordPoint(coords)') into @a;
PREPARE stmt1 FROM @a;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;
