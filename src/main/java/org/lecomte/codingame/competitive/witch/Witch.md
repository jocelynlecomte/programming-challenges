# Witch

## Changes

### 1.0 Wooden Ligue 2 (brew only)
- Find the potion with the highest price and BREW it (no check on inventory)

### 2.0 Wooden Ligue 1 (add cast)

- If a potion can be made, BREW IT.
- If a spell is unavailable, REST
- CAST the appliable spell which leads to the most balanced inventory
- else REST

A distance has been defined, between 2 pos. Actually we can think of potions and inventories as if they
were positions in a 4-D space. In this case the spells would be vectors which allow us to move from
one position to another.
And to brew a potion, it should be included in the volume created by the inventory.