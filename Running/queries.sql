

select count( Q.ID)
from(

	SELECT B.bidderID AS ID
	FROM Bidder B

	UNION

	SELECT S.sellerID AS ID
	FROM Seller S) AS Q;


select count(*)
from Item I, Location L
where L.location='New York' and L.locID=I.locID1	;


select count(Q.ID)
from (select IC.itemID AS ID
from Item_Category IC
group by IC.itemID
having COUNT(DISTINCT IC.categoryID)=4) AS Q;


SELECT I1.itemID
FROM  Item I1, (
	SELECT  MAX(I.currently) AS MAXPRICE
	FROM Item I
	WHERE  I.started < '2001-12-20 00:00:01'
	AND I.ends >= 	'2001-12-20 00:00:01' AND I.noOfBids > 0

	) AS Q
  WHERE I1.currently = Q.MAXPRICE AND I1.started < '2001-12-20 00:00:01'
	AND I1.ends >= 	'2001-12-20 00:00:01' AND I1.noOfBids >0	;



select count(S.sellerID)
from Seller S
where S.rating >1000;


select COUNT(DISTINCT S.sellerID)
from Seller S, Bidder B
where S.sellerID=B.bidderID;


select COUNT(DISTINCT Q.CAT)
	from (select   IT.categoryID AS CAT
	from Item I, Item_Category IT
	WHERE I.currently > 100 and I.noOfBids > 0 and I.itemID=IT.itemID
	) AS Q;






