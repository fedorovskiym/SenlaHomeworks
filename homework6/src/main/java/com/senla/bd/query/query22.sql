select speed, AVG(price::numeric) as average_price from pc
where speed > 600
group by speed