select product.maker, laptop.speed 
from product inner join laptop on product.model = laptop.model
where laptop.hd >= 100