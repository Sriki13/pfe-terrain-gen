# End of Study Project (PFE)

### [Composition of Terrain Generation algorithms \[Visualization algorithms\]](http://gmolines.github.io/pfe/oqp/al/ihm/2018/09/11/Y1819-S057)

**Id :** (Y1819-S057)
**Group Number :** 3
**Superviser :** Sébastien Mosser

# Team
 - **Guillaume André** (Sriki13)
 - **Lucas Matteo** (LucasUnice)
 - **Jean-Adam Puskaric** (JAMamene)

# Project Structure
## Algo
This module contains the **core** algorithm interfaces as well as the individual implementations as  standalone jar artifacts and maven source code.

## Composer
This is the composer of all the algorithms that has to find a suitable composition of implementations as well as manage parameters, dependencies.
It will then provide the necessary artifacts to **gen** in order to produce a lightweight and custom terrain generator. 

## Gen
This is the final module. It will provide an executable and publish a custom generator as a web service for easy parametrized island generation.
