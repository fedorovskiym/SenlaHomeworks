select maker from product 
where product.type = 'PC' 
and maker not in (
	select maker from product
	where product.type = 'Laptop'
)
group by maker