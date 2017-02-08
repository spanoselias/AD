select COUNT(DISTINCT S.sellerID)
from Seller S, Bidder B
where S.sellerID=B.bidderID;
