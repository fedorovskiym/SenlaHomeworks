select product.maker, MIN(price) from printer 
inner join product on product.model = printer.model
where color = 'y'
group by product.maker