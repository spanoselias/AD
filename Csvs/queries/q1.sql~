select count( Q.ID)
from(

	SELECT B.bidderID AS ID
	FROM Bidder B

	UNION 

	SELECT S.sellerID AS ID
	FROM Seller S) AS Q;
