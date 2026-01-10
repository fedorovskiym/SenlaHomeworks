select AVG(speed) as average_speed from laptop 
inner join product on product.model = laptop.model
where product.maker = 'A'