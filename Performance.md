# Performance Techniques
## Applied
### Indices
For searching,
    -> by pattern, included index of type btree-text-pattern-ops, 4 secs to 1ms..
    -> by pattern, checked whether if a result is possible with LIMIT 1, if yes, commit to search. (NOT NECESSARY WITH NON-COVERING INDEX), theres jitter with certain inputs, where the db resorts to sequential scan
    -> by pattern, use INCLUDE part of the index creation to include the data in the index without it being in the search key of the index
### LRU / MRU cache
Cache Eviction
    -> LRU
    -> also includes MRU for Batch Searching since once a batch is used, it's not required anymore
Cache Invalidation
    -> nodes have a max ttl of 5mins (attendance in my college takes place in the last 5 mins, so searching for students takes place under load in this time period)
To make the cache O(1)
    -> put tries to evict exactly one stale node every time it get's invoked, instead of trying to evict all stale nodes in the cache at once.
    -> get checks if the node is stale before returning
