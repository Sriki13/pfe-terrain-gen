
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

## Core
This module contains the core of Island Generation, namely the definition of the Contract, Mappable and Parameter interface, the Island Map as well as general purpose utility classes.

## Algo
This module contains algorithm implementations as individual maven projects. They are java classes which implements the Contract interface and apply a transformation to an IslandMap with a given Context.

## Composer
This is the composer of all the algorithms that has to find a suitable order of provided artifacts by using the Contract dependencies and starting context (order constraints).

## Gen
This is module contains the logic of an island generation. It contains the logic of execution and context application as well as all the benchmarking, logging and exception handling.

## Generator-Service
This module handles the networking and service publishing logic of the generator, as well as the orchestration logic which will check the provided algorithm compatibility using the composer, building the generator and forwarding the context to it.

## Generator-Factory
This module is tasked with facilitating the selection of map generation algorithms, dependency and context by serving all the available algorithms from an artifact repository.

# Installation & Launch


## Compile

In root directory

    mvn clean install

## Run
Go to compositions/{compositionToRun}

    mvn exec:java

Clean install optionally
The generator you ran is available at 

    localhost:8080



### The following routes are available :

**Info:**
 - **GET localhost:8080/parameters** : Get the available parameters of the generator with their type and description
 - **GET localhost:8080/algorithms** : Get the algorithms of the generator in the order they are executed
 - **GET localhost:8080/graph** : Get an SVG representation of the generator as a graph
 
 **Core:**
 - **POST localhost:8080/context** :  Post a JSON context (optional parameter values to use for the next executions)
 - **GET localhost:8080/execute** : Generate a map with the generator and the provided context and get the JSON representation
