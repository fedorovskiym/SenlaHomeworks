select product.type, laptop.model, speed from laptop 
inner join product on product.model = laptop.model
where speed < (select MIN(speed) from pc)