select product.model, laptop.price from product
inner join laptop on product.model = laptop.model
where product.maker = 'B'
union
select product.model, pc.price from product
inner join pc on product.model = pc.model
where product.maker = 'B'
union
select product.model, printer.price from product
inner join printer on product.model = printer.model
where product.maker = 'B'