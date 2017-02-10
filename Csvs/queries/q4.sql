
SELECT I1.itemID, I1.started, I1.ends 
FROM  Item I1, (
	SELECT  MAX(I.currently) AS MAXPRICE
	FROM Item I, Bid_Act BA
	WHERE I.itemID = BA.itemID AND I.currently = amount AND I.started < '2001-12-20 00:00:01' 
	AND I.ends > 	'2001-12-20 00:00:01'	 	
	  
	) AS Q
WHERE I1.currently = Q.MAXPRICE;



 
