# Prova Finale Ingegneria del Software 2020
# Santorini

## Gruppo AM16


- ###   10570407    Alessandro Astone ([@aleasto](https://github.com/aleasto))<br>10570407@polimi.it
- ###   10567628    Giacomo Brosolo ([@GiacoBros](https://github.com/GiacoBros))<br>10567628@polimi.it
- ###   10619706    Andrea D'Iapico ([@AndreaDIapico](https://github.com/AndreaDIapico))<br>10619706@polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Advanced Gods | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Undo | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

## How to run
`mvn clean assembly:single` produce un _fat_ jar in `./target`. Altrimenti, troverete un jar precompilato in `./DELIVERABLES`

Double click sul jar avvia il client GUI.

Altrimenti, il jar accetta dei parametri da riga di comando
* `java -jar AM16-1.0-SNAPSHOT-jar-with-dependencies.jar client gui` avvia il client gui
* `java -jar AM16-1.0-SNAPSHOT-jar-with-dependencies.jar client cli` avvia il client cli
* `java -jar AM16-1.0-SNAPSHOT-jar-with-dependencies.jar server` avvia il server

Lanciando il server verrà creato in `$(pwd)`, se non già esistenti, il file di configurazione `gods.josn` che racchiude
i dati sulle divinità, e il file di configurazione `server.conf` da cui è possibile modificare parametri come la porta di ascolto, o le regole di gioco di default.
Il file viene letto una volta all'avvio; modifiche successive al file saranno riflesse solo dopo un riavvio del programma.

Allo stesso modo, lanciando la GUI viene creato il file di configurazione `gui.conf` che stabilisce i valori di default
degli elementi di UI configurabili, come ip e porta del server, o i parametri di configurazione della partita.
