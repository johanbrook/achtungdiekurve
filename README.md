# Achtung Die Kurve

A multiplayer game written in Javascript and Java, using WebSockets and Canvas.

The system is completely directed by the server. I.e., all logic resides in Java code, and the client is merely a visual representation of the game world. Even the client redrawing is controlled by the server (over WebSockets). This may or may not be a good thing, but we wanted to try it out.

## Usage

This project is using Maven for building and dependency management.

```bash
mvn clean compile assembly:single
```

A `.jar` file is available in the `target` directory.
```bash
java -jar target/achtungdiekurve-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
Open `index.html` in a modern browser (you have to tweak the `config.host` value to `localhost` in order to play locally) and play with friends!

## To do

- Specify which server to connect to

## Authors

Client code by Johan. Backend by Bohn. Written while lining up for tickets, when we were horribly bored and had nothing better to do.
