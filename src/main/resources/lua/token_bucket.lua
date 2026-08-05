-------------------------------------------------------
-- Token Bucket Rate Limiter
-------------------------------------------------------

-- KEYS[1] = bucket:user-1

-- ARGV[1] = capacity
-- ARGV[2] = refill rate (tokens/sec)
-- ARGV[3] = current time (milliseconds)
-- ARGV[4] = requested tokens

local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-------------------------------------------------------
-- Read bucket state
-------------------------------------------------------

local tokens = tonumber(redis.call("HGET", key, "tokens"))
local lastRefill = tonumber(redis.call("HGET", key, "last_refill"))

-------------------------------------------------------
-- First request
-------------------------------------------------------

if tokens == nil then
    tokens = capacity
    lastRefill = now
end

-------------------------------------------------------
-- Calculate refill
-------------------------------------------------------

local elapsedSeconds = (now - lastRefill) / 1000.0

local refill = elapsedSeconds * refillRate

tokens = math.min(capacity, tokens + refill)

-------------------------------------------------------
-- Consume tokens
-------------------------------------------------------

local allowed = 0

if tokens >= requested then
    tokens = tokens - requested
    allowed = 1
end

-------------------------------------------------------
-- Persist latest state
-------------------------------------------------------

redis.call(
    "HSET",
    key,
    "tokens",
    tokens,
    "last_refill",
    now
)

-------------------------------------------------------
-- Auto expire idle bucket
-------------------------------------------------------

local ttl = math.ceil((capacity / refillRate) * 2)

redis.call("EXPIRE", key, ttl)

-------------------------------------------------------
-- Return
-------------------------------------------------------

return {
    allowed,
    math.floor(tokens),
    capacity,
    ttl
}