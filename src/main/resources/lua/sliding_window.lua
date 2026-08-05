---------------------------------------------------------
-- Sliding Window Log
---------------------------------------------------------

-- KEYS[1] = rate:user1

-- ARGV[1] = currentTimeMillis
-- ARGV[2] = windowSizeMillis
-- ARGV[3] = maxRequests

local key = KEYS[1]

local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])

---------------------------------------------------------
-- Remove expired requests
---------------------------------------------------------

redis.call(
    "ZREMRANGEBYSCORE",
    key,
    0,
    now - window
)

---------------------------------------------------------
-- Count requests in current window
---------------------------------------------------------

local currentCount = redis.call("ZCARD", key)

---------------------------------------------------------
-- Check limit
---------------------------------------------------------

local allowed = 0

if currentCount < limit then

    allowed = 1

    redis.call(
        "ZADD",
        key,
        now,
        tostring(now)
    )

    currentCount = currentCount + 1
end

---------------------------------------------------------
-- Expire Redis Key
---------------------------------------------------------

local ttl = math.ceil(window / 1000)

redis.call("EXPIRE", key, ttl)

---------------------------------------------------------
-- Return
---------------------------------------------------------

return {
    allowed,
    currentCount,
    limit,
    ttl
}