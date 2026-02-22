select maker from product 
inner join pc on product.model = pc.model
where pc.speed >= 450
group by maker