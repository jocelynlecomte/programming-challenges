# Ghost In the Cell

## Changes

### 1.0
Take the first friendly factory in the list and send all troups to the first enemy factory

### 2.0
- Refactored to implement classes
- For each friendly factory, find the closest neutral factory or by default the closest enemy
factory and attack with full force

### 3.0
- Refactored to first read input then make decisions instead of making decisions while
reading, because this lead to wrong decisions, especially on first turn
- Ignored no-production factories
- Allow a factory to launch several attacks. For each friendly factory:
 1. create a list of neutral factories then enemy factories ordered by distance
 2. send each turn just enough cyborgs to overwhelm target factory asap

## Ideas
- Use the bombs to spot early the biggest enemy factories.