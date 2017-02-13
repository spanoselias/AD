
SELECT I1.itemID
FROM  Item I1, (
	SELECT  MAX(I.currently) AS MAXPRICE
	FROM Item I
	WHERE  I.started < '2001-12-20 00:00:01' 
	AND I.ends >= 	'2001-12-20 00:00:01' AND I.noOfBids > 0	 	
	  
	) AS Q
WHERE I1.currently = Q.MAXPRICE AND I1.started < '2001-12-20 00:00:01' 
	AND I1.ends >= 	'2001-12-20 00:00:01' AND I1.noOfBids >0	;



 
