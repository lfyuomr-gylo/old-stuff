N = arg[1] or 1200
N = tonumber(N)

function range(n)
    return coroutine.wrap(function ()
        for i = 2, n do coroutine.yield(i) end
    end)
end

function sift(range, mod)
    return coroutine.wrap(function ()
        while true do
            cur = range()
            if cur == nil then return end
            -- print('sift mod ' .. mod .. '\ncur ' .. cur)
            if cur % mod ~= 0 then coroutine.yield(cur) end
        end
    end)
end

function get_primes( range )
    result = {}
    while true do
        cur = range()
        if cur == nil then break end
        table.insert(result, cur)
        range = sift(range, cur)
    end
    return result
end

for i = 1, 10 do
	r = range(N)
	res = get_primes(r)
end
