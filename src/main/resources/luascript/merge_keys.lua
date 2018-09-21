local index = cjson.decode(ARGV[1]).index
local old_key = KEYS[1] .. ':temp:set'

if #KEYS >= index then
    local this_key = KEYS[index]
    local this_exists = KEYS[index] .. ':temp:set:exists'
    local exists = redis.call('exists', this_exists)
    if exists == 1 then
        return
    end
    local map_keys = redis.call('hkeys', this_key)
    for i, v in ipairs(map_keys) do
        redis.call('sadd', old_key, v)
    end
    redis.call('set', this_exists, 1)
else
    local list_key = cjson.decode(ARGV[1]).listKey
    local exists = redis.call('exists', list_key)
    if exists == 1 then
        return
    end

    local set_values = redis.call('smembers', old_key)
    for i, v in ipairs(set_values) do
        redis.call('lpush', list_key, cjson.encode(v))
    end
    redis.call('del', old_key)
    for i, v in ipairs(KEYS) do
        local key = v .. ':temp:set:exists'
        redis.call('del', key)
    end
end

