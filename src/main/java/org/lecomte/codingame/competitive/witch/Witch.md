# Witch

## Changes

### 1.0 Wooden Ligue 2 (brew only)
- Find the potion with the highest price and BREW it (no check on inventory)

### 2.0 Wooden Ligue 1 (add cast)

- If a potion can be made, BREW it.
- If a spell is unavailable, REST
- CAST the appliable spell which leads to the most balanced inventory
- else REST

A distance has been defined, between 2 pos. Actually we can think of potions and inventories as if they
were positions in a 4-D space. In this case the spells would be vectors which allow us to move from
one position to another.
And to brew a potion, it should be included in the volume created by the inventory.

### 3.0 Bronze league (add LEARN and tons of things)

- If a potion can be made, BREW it (and prefer the one with max price).
- Else if a worthy spell (i.e. a spell that is only positive) is available, and we can afford it, LEARN it.
- Else 
    1. generate inventories with timeout or a path to every potion
        - Generate a graph of inventories reachable by casting spells on current inventory (with timeout or until we
        have a path to every potion)
        - Retain paths which allow to brew a new potion.
        - It gives a list of paths which allow to brew the higher-priced potion, select the shortest one
    2. Chose in the retained paths the one leading to the most rewarding potion and CAST the first spell of the path
- Else we don't have any path, so if a spell is castable, CAST it
- Else REST

### 3.1 Silver league
- If a potion can be made, BREW it (and prefer the one with max price).
- Else if we don't have 8 spells, or if there is a worthy spell, LEARN it
- Else 
    1. generate inventories (checking time and memory) until we have one path for every potion
        - Generate a graph of inventories reachable by casting spells on current inventory 
        - Retain paths which allow to brew a new potion.
        - It gives a path for each potion, which should be the shortest one
    2. Chose in the retained paths the one leading to the most rewarding potion and CAST the first spell of the path
- If there are retained paths, find the one leading to the most rewarding potion, get the first spell and CAST it
if possible, else REST
- Else we don't have any path, so if a spell is castable, CAST it
- Else REST


### Ideas
- When we want to try a path and the first spell is exhausted, try to CAST another one in the path instead of REST
- The path can change every turn, so it may be that we are heading to an expensive potion and if we find something
brewable, we BREW it, even if it's a poor potion. Try to retain the path from one turn to the other, or at least
 avoid poor potions ?
