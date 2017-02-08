select count(*)
from Item I 
where  
	 I.locID1 IN (  select locID
			    from Location 
			   where location = 'New York')	;
