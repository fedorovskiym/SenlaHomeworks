select product.maker, AVG(screen) from laptop
inner join product on product.model = laptop.model 
group by product.maker