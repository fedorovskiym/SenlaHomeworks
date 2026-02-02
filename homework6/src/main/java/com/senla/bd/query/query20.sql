select maker, COUNT(distinct model) from product
where type = 'PC'
group by maker
having COUNT(model) >= 3