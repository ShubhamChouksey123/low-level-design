# Connection Pool

## Actor 
- Any process

## Functional Requirements

- Should be able to create a connection pool of fixed size
- A process should be to get an idle connection from pool
- In case all the connection are occupied and a request comes from a process for a connection,
  the request should be added in a request queue.
- An acquired connection can be released, and the released connection can be assigned to the first process
  in the request queue.

## Entities 
- Connection
- ConnectionStatus (OCCUPIED / IDLE)
- ConnectionPool
- Request
- ConnectionPoolCreationService
- ConnectionAssigningStrategy
- FirstEmptyConnectionAssigningStrategy