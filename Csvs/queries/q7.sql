select COUNT(DISTINCT Q.CAT)
	from (select   IT.categoryID AS CAT
	from Item I, Item_Category IT
	WHERE I.currently > 100 and I.noOfBids > 0 and I.itemID=IT.itemID
	) AS Q;
