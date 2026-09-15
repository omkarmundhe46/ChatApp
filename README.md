# ChatApp — Java Socket Chat Application

A simple multi-client chat application built with **Java Socket Programming** and **Java Swing**. The project demonstrates how a server accepts TCP client connections, handles clients concurrently with threads, and broadcasts messages to connected clients through a graphical desktop interface.

## Overview

ChatApp follows a classic client-server architecture:

- The **Chat Server** listens on TCP port `12345`.
- Multiple clients can connect to the server simultaneously.
- Each connected client is handled on a separate thread.
- Messages received from one client are broadcast to the other connected clients.
- Both server and client provide a **Swing GUI** for sending and viewing messages.

The implementation is intentionally lightweight and is useful for learning **Java networking, sockets, streams, multithreading, and Swing event handling**.

## Features

- Java Swing-based desktop chat interface
- TCP socket communication
- Multi-client support
- Concurrent client handling with threads
- Server-side message broadcasting
- Send messages from the client or directly from the server GUI
- Live connection and message activity shown in the server window
- Enter key support for sending messages
- Automatic cleanup when a client disconnects

## Architecture

```text
                    TCP / Socket : 12345

        +-------------------------------+
        |         Chat Server           |
        |       ChatServerGUI           |
        |                               |
        |  ServerSocket.accept()        |
        |          |                    |
        |     +----+----+               |
        |     |         |               |
        | ClientHandler ClientHandler   |
        |   Thread #1     Thread #2     |
        +-----|-------------|-----------+
              |             |
         +----+----+   +----+----+
         | Client 1|   | Client 2|
         | Swing UI|   | Swing UI|
         +---------+   +---------+
```

## Technology Stack

| Technology | Usage |
|---|---|
| Java | Core application language |
| Java Swing | Client and server desktop GUIs |
| Java Networking | `Socket` and `ServerSocket` TCP communication |
| Java I/O | `BufferedReader`, `PrintWriter`, input/output streams |
| Multithreading | One handler thread per connected client |
| Collections | Tracking connected clients with a `HashSet` |

No external libraries or build framework are required.

## Project Structure

```text
ChatApp/
├── CharClientGUI.java
├── ChatServerGUI.java
├── ClientHandler.java
└── README.md
```

### `CharClientGUI.java`

The desktop client application. It:

- Creates the Swing chat window.
- Connects to `localhost:12345`.
- Sends messages through a `PrintWriter`.
- Reads incoming messages on a background thread.
- Displays received messages in the chat area.

### `ChatServerGUI.java`

The main server application. It:

- Opens a `ServerSocket` on port `12345`.
- Accepts incoming client connections.
- Creates a handler thread for each connection.
- Maintains connected clients in a `HashSet`.
- Broadcasts received messages to other clients.
- Allows the server operator to send messages to connected clients.

### `ClientHandler.java`

A standalone socket handler implementation that reads client messages and delegates broadcasting/removal to a `Server` class.

> **Repository note:** the current repository does not contain a `Server.java` implementation. The runnable server logic is currently implemented inside `ChatServerGUI.java`, including its own inner `ClientHandler`. Therefore, `ClientHandler.java` should be treated as an alternate/older handler implementation unless a matching `Server` class is added.

## How Message Broadcasting Works

When a client sends a message:

1. `CharClientGUI` writes the message to the socket using `PrintWriter`.
2. The server-side client handler reads the message with `BufferedReader`.
3. `ChatServerGUI.broadcastMessage(...)` iterates over the connected clients.
4. The message is sent to every connected client except the sender.
5. Each client receives the message on its background reader thread and appends it to the GUI.

## Prerequisites

- Java JDK 8 or later
- A desktop environment with Java Swing support
- Local network access when running clients on different machines

You can verify Java with:

```bash
java -version
javac -version
```

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/omkarmundhe46/ChatApp.git
cd ChatApp
```

### 2. Compile the main application classes

```bash
javac ChatServerGUI.java CharClientGUI.java
```

You can also compile the standalone handler when experimenting with it:

```bash
javac ClientHandler.java
```

### 3. Start the server

Run:

```bash
java ChatServerGUI
```

The server starts listening on TCP port `12345`.

### 4. Start one or more clients

Open another terminal and run:

```bash
java CharClientGUI
```

Repeat the command in additional terminals to connect multiple clients.

All clients on the same machine can use `localhost` because the client is configured with:

```java
private static final String SERVER_ADDRESS = "localhost";
private static final int SERVER_PORT = 12345;
```

## Running Across Multiple Machines

To connect from another machine on the same network:

1. Start `ChatServerGUI` on the server machine.
2. Find the server machine's local IP address.
3. Update `SERVER_ADDRESS` in `CharClientGUI.java` from `localhost` to the server IP.
4. Make sure TCP port `12345` is allowed through the server machine's firewall.
5. Compile and run the client on the other machine.

Example:

```java
private static final String SERVER_ADDRESS = "192.168.1.10";
private static final int SERVER_PORT = 12345;
```

## Core Java Concepts Demonstrated

### Socket Programming

The project uses `ServerSocket` to accept incoming connections and `Socket` for client-server TCP communication.

### Multithreading

Each accepted client is processed independently using a new `Thread`, allowing several clients to communicate concurrently.

### Java I/O

`BufferedReader` is used for reading newline-delimited messages and `PrintWriter` is used for sending messages.

### Swing Event Handling

The GUI uses Swing components such as `JFrame`, `JTextArea`, `JTextField`, `JButton`, and `JScrollPane`, with action listeners for button and Enter-key events.

### Shared Client Management

The server tracks active handlers in a `HashSet` and removes handlers when their socket connection closes.

## Current Implementation Notes

- The server uses a fixed port: `12345`.
- The client uses a fixed server address: `localhost`.
- Messages are plain text and newline-delimited.
- There is no user authentication or identity management.
- There is no message persistence or database integration.
- There is no TLS/encryption layer, so the application should be considered suitable for learning/local experimentation rather than production use.
- The Swing UI is intentionally simple and focuses on demonstrating networking concepts.

## Known Codebase Inconsistency

The repository currently contains two client-handler patterns:

1. An **inner `ClientHandler`** inside `ChatServerGUI.java`, which is the implementation used by the provided server.
2. A separate top-level **`ClientHandler.java`**, which calls `Server.broadcastMessage(...)` and `Server.removeClient(...)`.

Because `Server.java` is not currently present, the standalone `ClientHandler.java` is not self-contained. A future cleanup could either remove the unused file or introduce a dedicated `Server` class and refactor the application around it.

## Possible Improvements

For a stronger production-style version, the project could be extended with:

- Usernames and private messaging
- Login/authentication
- Message timestamps
- Persistent chat history using a database
- File and image sharing
- Client disconnect notifications
- Graceful server shutdown
- ExecutorService/thread-pool based connection handling
- Thread-safe client collection such as `ConcurrentHashMap` or a synchronized collection
- Configurable host and port through command-line arguments or configuration files
- TLS/SSL sockets for encrypted communication
- Improved Swing UI using a clearer chat layout
- Unit and integration tests
- Maven or Gradle build configuration

## Learning Outcomes

This project is a practical example for understanding:

- TCP/IP client-server communication
- `ServerSocket` vs `Socket`
- Blocking I/O and streams
- Concurrent client handling
- Broadcasting data between clients
- Java Swing GUI programming
- Event-driven programming
- Resource cleanup and socket lifecycle management

## Author

**Omkar Ramesh Mundhe**

GitHub: [@omkarmundhe46](https://github.com/omkarmundhe46)

## License

No explicit license is currently defined in the repository. Add a `LICENSE` file if you plan to distribute the project under a specific open-source license.
