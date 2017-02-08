select COUNT(DISTINCT Q.CAT)
	from (select   IT.categoryID AS CAT
	from Item I, Item_Category IT
	where I.currently > 100 and I.itemID=IT.itemID
	GROUP BY I.itemID) AS Q;
