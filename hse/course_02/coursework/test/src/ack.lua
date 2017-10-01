function ackerman(n, x, y )
	if (n == 0) then 
		return y + 1
	end
	if (y == 0 and n == 1) then 
		return x
	end
	if (y == 0 and n == 2) then 
		return 0
	end
	if (y == 0 and n > 2) then 
		return 1
	end

	return ackerman(n - 1, x, ackerman(n, x, y - 1))
end

ackerman(5, 2, 3)