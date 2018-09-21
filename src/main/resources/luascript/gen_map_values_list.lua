
local list_key = cjson.decode(ARGV[1])
print("list_key", list_key)
local exists = redis.call('exists', list_key)
if exists == 1 then
    return
end
local map_values = redis.call('hvals', KEYS[1])
if map_values == nil then
    print("map values is null")
    return
end

for i, v in ipairs(map_values) do
    redis.call('lpush', list_key, v)
end

