select count(*)
from Item I, Location L 
where L.location='New York' and L.locID=I.locID1	;