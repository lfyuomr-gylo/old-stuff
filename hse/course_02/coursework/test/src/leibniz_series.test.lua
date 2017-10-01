function series( it )
	result = 0
	cur = 1
	for i = 0, it do
		result = result + 1 / (2 * i + 1) * cur
		cur = -1 * cur;
	end
	return result
end
for i = 1, 10 do
	series(1000000)
end