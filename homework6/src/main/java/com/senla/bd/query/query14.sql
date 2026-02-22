select speed, AVG(price::numeric) AS average_price FROM pc
group by speed