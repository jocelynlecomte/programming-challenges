# PacMan

## Changes

### 1.0
Find a random super pellet and go eat it

### 2.0
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
  - Targeting strategy
    - If the previous target is still valid, keep it
    - To choose a new target
      1. take super pellets
      2. choose the most rewarding line of sight


## Ideas
- Apply speed boost to each friendly pac at the start of the game
- Get a pellet in sight when a pac has to choose a new target, before a random cell
- Get a list of all previous positions in order to get a more appropriate target
- Implement a pathfinding algorithm to be able to compute a distance.
