select hd from pc
group by hd 
having COUNT(*) >= 2 
