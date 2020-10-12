# CONSEGNE
## 1 (slide 39) 
**Consegna:** Produrre lo spazio comportamentale di una rete finita di graph.fa.graph.fa comportamentali data.

### Automa a stati (graph.fa.graph.fa = Finite Automata)
- insieme non vuoto di **stati** e insieme (anche vuoto) di **transizioni** ✅
- un solo **stato iniziale** e un insieme di **stati di accettazione** (anche quello iniziale può esserlo) ✅
- ad ogni transizione corrisponde un simbolo di un alfabeto ✅
- convertibile in Regex che descrive il linguaggio accettato dal graph.fa.graph.fa (algoritmo: EspressioneRegolare)  ✅
- poter ricavare linguaggio accettato (algoritmo: EspressioniRegolari) ⚠️

#### Osservazioni
- le transizioni hanno un simbolo associato, ma quando eseguo EspressioneRegolare
vado ad associare delle regex alle transizioni. Conviene forse considerare il
simbolo iniziale anch'esso come una regex?
- in EspressioneRegolare, al posto di cercare **sequenze** di nodi con inDegree=outDegree=1 possiamo elaborare un singolo nodo 
ad ogni iterazione del ciclo while ()
- EspressioniRegolari, per ciascun stato di accettazione marca la corrispondente
regex con un pedice. Come lo si codifica?

### 👨🏻‍💻 graph.fa.graph.fa comportamentale (estensione di graph.fa.graph.fa)
- non ci sono stati di accettazione
- transizione è dotata di un evento di ingresso (anche nullo) e di un insieme di eventi di uscita (anche vuoto)
- transiz. possono avere stesso stato sorgente/destinazione (mentre con graph.fa.graph.fa no).
- è un graph.fa.graph.fa non deterministico (NFA) sull'alfabeto degli eventi in ingresso (
forse, al posto di avere una classe con l'attributo inEvent per rappresentare
l'evento in ingresso, potremmo riciclare la classe Transizione e sfruttare 
il suo attributo "symbol" per rappresentare l'eventoin ingresso 
che sarà accessibile con un metodo getInEvent).

### Rete di graph.fa.graph.fa comportamentali
- nodo = graph.fa.graph.fa comportamentale
- connessioni = link (anche paralleli: stessa sorgente e destinaz.)
- link sono buffer di capacità unitaria (vuoto oppure 1)
- ciascuna transiz. di un graph.fa.graph.fa **può** essere dotata di eventi in ingresso
e **può** generare eventi in uscita. 
- lo scatto di una transiz. dipende dallo stato dei link
- stato (della rete) = stato dei suoi componenti e dei link (contenuto del link). Può essere iniziale, finale o nessuno dei due

#### Osservazioni
 è un graph.fa.graph.fa comportamentale, significa che serve le transizioni uscenti dallo stato iniziale?

### Spazio comporamentale di una rete di graph.fa.graph.fa
è un graph.fa.graph.fa **deterministico**
- simboli: identificatori delle transizioni dei componenti della rete

**Requisiti spazio comportamentale:**
- Etichettabile  
- Potabile  



## 2 (slide 46)
**Consegna:** Produrre lo spazio comportamentale di una rete finita di graph.fa.graph.fa comportamentali relativo ad un'osservazione lineare data.

## 3 (slide 55)
**Consegna:** Dato spazio comportamentale di un'osservazione lineare, produrre diagnosi relativa.

## 4 (slide 73)
**Consegna:** Dato lo spazio comportamentale e stato d'ingresso (o lo stato iniziale oppure stato con transiz. osservabili
entranti),  produrre la sua chiusura silenziosa.
Produrre il diagnosticatore.

## 5 (slide 86)