select pc1.model, pc2.model, pc1.speed, pc1.ram from pc pc1
join pc pc2 on pc1.speed = pc2.speed and pc1.ram = pc2.ram and pc1.model > pc2.model