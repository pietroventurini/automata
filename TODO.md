# CONSEGNE
## 1 (slide 39) 
**Consegna:** Produrre lo spazio comportamentale di una rete finita di fa comportamentali data.

### Automa a stati (fa = Finite Automata)
- insieme non vuoto di **stati** e insieme (anche vuoto) di **transizioni** ✅
- un solo **stato iniziale** e un insieme di **stati di accettazione** (anche quello iniziale può esserlo) ✅
- ad ogni transizione corrisponde un simbolo di un alfabeto ✅
- convertibile in Regex che descrive il linguaggio accettato dal fa (algoritmo: EspressioneRegolare)  ✅
- poter ricavare linguaggio accettato (algoritmo: EspressioniRegolari) ⚠️

#### Osservazioni
- le transizioni hanno un simbolo associato, ma quando eseguo EspressioneRegolare
vado ad associare delle regex alle transizioni. Conviene forse considerare il
simbolo iniziale anch'esso come una regex?
- in EspressioneRegolare, al posto di cercare **sequenze** di nodi con inDegree=outDegree=1 possiamo elaborare un singolo nodo 
ad ogni iterazione del ciclo while ()
- EspressioniRegolari, per ciascun stato di accettazione marca la corrispondente
regex con un pedice. Come lo si codifica?
- Valutare se validare solo il MutableNetwork al posto di FA prima di istanziare
quest'ultimo all'interno del builder (altrimenti rischieremmo di
creare un FA non valido, ad esempio con più stati iniziali, causando un'eccezione
ancora in fase di costruzione con `retrieveInitialState)`.

### 👨🏻‍💻 fa comportamentale (estensione di fa)
- non ci sono stati di accettazione
- transizione è dotata di un evento di ingresso (anche nullo) e di un insieme di eventi di uscita (anche vuoto)
- transiz. possono avere stesso stato sorgente/destinazione (mentre con fa no).
- è un fa non deterministico (NFA) sull'alfabeto degli eventi in ingresso (
forse, al posto di avere una classe con l'attributo inEvent per rappresentare
l'evento in ingresso, potremmo riciclare la classe Transizione e sfruttare 
il suo attributo "symbol" per rappresentare l'eventoin ingresso 
che sarà accessibile con un metodo getInEvent).

### Rete di fa comportamentali
- nodo = fa comportamentale
- connessioni = link (anche paralleli: stessa sorgente e destinaz.)
- link sono buffer di capacità unitaria (vuoto oppure 1)
- ciascuna transiz. di un fa **può** essere dotata di eventi in ingresso
e **può** generare eventi in uscita. 
- lo scatto di una transiz. dipende dallo stato dei link
- stato (della rete) = stato dei suoi componenti e dei link (contenuto del link). Può essere iniziale, finale o nessuno dei due

#### Osservazioni
 è un fa comportamentale, significa che serve le transizioni uscenti dallo stato iniziale?

### Spazio comporamentale di una rete di fa
è un fa **deterministico**
- simboli: identificatori delle transizioni dei componenti della rete

**Requisiti spazio comportamentale:**
- Etichettabile  
- Potabile  



## 2 (slide 46)
**Consegna:** Produrre lo spazio comportamentale di una rete finita di fa comportamentali relativo ad un'osservazione lineare data. ✅

## 3 (slide 55)
**Consegna:** Dato spazio comportamentale di un'osservazione lineare, produrre diagnosi relativa
ad un osservazione lineare data.

## 4 (slide 73)
**Consegna:** Dato lo spazio comportamentale e stato d'ingresso (o lo stato iniziale oppure stato con transiz. osservabili
entranti), produrre la sua chiusura silenziosa.
Produrre il diagnosticatore.

1. **chiusura silenziosa di uno stato s**: è sottospazio dello spazio comportamentale
contenente tuttii gli stati raggiungibili da s tramite cammini non osservabili
2. **chiusura silenziosa decorata**:  
    2.1. parto da chiusura silenziosa di uno stato s.  
    2.2. Considero tutti gli s' che siano finali o con transiz. osservabili uscenti.  
    2.3. Invoco EspressioniRegolari su FA dove `stato_iniziale = s`, `stati_accettazione = tutti gli s' `.
3. **Diagnosi relativa a una chiusura silenziosa**: OR delle sue decorazioni.

## 5 (slide 86)