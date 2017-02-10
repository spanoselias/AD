select count(Q.ID)
from (select IC.itemID AS ID
from Item_Category IC
group by IC.itemID
having COUNT(DISTINCT IC.categoryID)=4) AS Q;