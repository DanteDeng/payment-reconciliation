local list_key = cjson.decode(ARGV[1])

local exists = redis.call('exists', list_key)
if exists == 1 then
    return
end

local set_key = list_key .. ":set"
for i, v in ipairs(KEYS) do
    local map_keys = redis.call('hkeys', v)
    for e, f in ipairs(map_keys) do
        redis.call('sadd', set_key, f)
    end
end

local set_values = redis.call('smembers', set_key)
for i, v in ipairs(set_values) do
    redis.call('lpush', list_key, cjson.encode(v))
end

redis.call('del', set_key)

