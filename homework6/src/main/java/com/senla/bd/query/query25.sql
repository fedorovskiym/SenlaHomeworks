select maker from product
inner join pc on product.model = pc.model
where pc.ram = (select MIN(ram) from pc)
and pc.speed = (select MAX(speed) from pc where ram = (select MIN(ram) from pc))
and product.maker in (select product.maker from product inner join printer on printer.model = printer.model);