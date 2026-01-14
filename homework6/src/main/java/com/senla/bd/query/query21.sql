select product.maker, MAX(price::numeric) from pc
inner join product on product.model = pc.model
group by product.maker