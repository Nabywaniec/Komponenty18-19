# Komponenty18-19

## Wstęp
Projekt realizowany w ramach przedmiotu "Technologie komponentowe". Celem projektu jest implementacja rozwiązania multi travelling salesman problem(mtsp), czyli problemu komiwojażera dla wielu pojazdów używajac list przekierowań i algorytmów genetycznych. Wykorzystany zostanie framework jMetal, który udostępnia gotowe implementacje algorytmów genetycznych.

## Model

### Graf
Do reprezentacji problemu komiwojażera postanowiliśmy użyć grafu nieskierowanego, gdzie każdy wierzchołek reprezentuje miejsce do którego należy dojechać, a krawędź drogę pomiędzy tymi miejscami. W wierzchołkach będą znajdować się listy przekierowań. W krawędziach znajdują się takie informacje jak odległość między wierzchołkami.

### Listy przekiereowań
Lista przekierowań to struktura danych znajdująca się w każdym wierzchołku grafu. Lista ta składa się z identyfikatorów węzłów, które sąsiadują z zadanym węzłem. Są to przekierowania, czyli informacja, do którego sąsiada tego wierzchołka ma się udać pojazd, który właśnie wjechał na ten wierzchołek. Lista przekierowań składa się z określonej liczby elementów, pierwszy pojazd, który wjechał na dany wierzchołek kieruje się do sąsiada wskazywanego przez pierwszy element listy przekierowań, drugi pojazd korzysta z drugiego elementu listy, lista jest zapętlona.

### Krzyżowanie
Zostały zaimplementowane następujące algorytmy krzyżowania:
- losowe wybranie elementów list przekierowań z dwóch osobników które zostają następnie wymienione pomiędzy nimi.

### Mutacje
Zostały zaimplementowane następujące algorytmy mutowania:
- losowe wymieszanie elementów list przekierowań danego osobnika.

## Użytkowanie programu

## Referencie
Agata Kubiczek, "Optymalizacja planów poszukiwania intruzów w budynku przez grupę robotów mobilnych"
