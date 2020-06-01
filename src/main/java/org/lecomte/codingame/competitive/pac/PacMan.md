# PacMan

## Changes

### 1.0
Find a random super pellet and go eat it

### 2.0
- New rule: team of pacs
- Changes
  - Each friendly pac remembers its target and keeps it until its reached or has disappeared.
  - To pick a new target, choose a non-targeted super-pellet (or simple pellet if none
 available)

### 3.0 (Bronze)
- New rules
  - Limited visibility
  - Speed boost
  - Pacs have a type

- Changes
  - Recreate pacs from the input each turn
  - Compute line of sights of each friendly pac
  - Remember where it is impossible to find a pellet in case we have to choose a random cell
  - Change type when enemy is too close to avoid being eaten
  - Apply speed boost if no close enemy
  - Targeting strategy
    - If the previous target is still valid, keep it
    - To choose a new target
      1. take super pellets
      2. choose the most rewarding line of sight

### 4.0
- Implemented pathfinding algorithm, and use it to compute distance instead of squared distance
- 

## Ideas
- Instead of finding the closest super pellet for each friendly pac ordered by id,
walk throught the super pellets and try to find the closest friendly pac
