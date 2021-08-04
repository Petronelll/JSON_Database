<h1>JSON Database</h1>

This project is a client-server application that allows the clients to store their data on the server in JSON format. The server's work is parallelized using executors so that every request is parsed and handled in a separate executor's task.

<h2>Usage</h2>

Firstly start the server by running the main method of the **server/Main** class. After that, you can start sending requests to the server by running the main method of the **client/Main** class. Data is sent and received between different processes using a socket.

There are two possibilities to send a request:

- Create it using command line parameters: 
  - `-t` the type of the request
  - `-k` the key
  - `-v` is the value to save in the database

- Read it from a file located in **client/data** using the `-in` parameter

The request types are: get, set, delete and exit. Exit is used to stop the server.

<h2>Commands</h2>

The database supports three operations:

- GET
- SET
- DELETE

Examples:

```
> java Main -t get -k 1
Client started!
Sent: {"type":"get","key":"1"}
Received: {"response":"ERROR","reason":"No such key"}
```

```
> java Main -t set -k 1 -v HelloWorld! 
Client started!
Sent: {"type":"set","key":"1","value":"HelloWorld!"}
Received: {"response":"OK"}
```

```
> java Main -t get -k 1 
Client started!
Sent: {"type":"get","key":"1"}
Received: {"response":"OK","value":"HelloWorld!"}
```

```
> java Main -in testSet.json 
Client started!
Sent: {"type":"set","key":"name","value":"Kate"}
Received: {"response":"OK"}
```

```
> java Main -in testGet.json 
Client started!
Sent: {"type":"get","key":"name"}
Received: {"response":"OK","value":"Kate"}
```

```
> java Main -in testDelete.json 
Client started!
Sent: {"type":"delete","key":"name"}
Received: {"response":"OK"}
```

```
> java Main -t exit 
Client started!
Sent: {"type":"exit"}
Received: {"response":"OK"}
```

<h2>Database</h2>

The server keeps the database on in the `db.json` file, located in the **/server/data/** folder, and updates it only after setting a new value or deleting one. I used `java.util.concurrent.locks.ReentrantReadWriteLock` class to allow multiple readers of the resource but only a single writer. The database is able to store any JSON objects as values.

<h2>Complex keys</h2>

The server supports requests with complex keys. For example, in the code snippet below, the user wants to get only the surname of the person:

```no-highlight
{
    ... ,

    "person": {
        "name": "Adam",
        "surname": "Smith"
    }
    ...
}
```

Then, the user should type the full path to this field in a form of a JSON array: `["person", "surname"]`. If the user wants to get the full `person` object, then they should type `["person"]`. The user is able to set separate values inside JSON values. For example, it is possible to set only the surname using a key `["person", "surname"]` and any value including another JSON. Moreover, the user is able to set new values inside other JSON values. For example, using a key `["person", "age"]` and a value `25`, the `person` object should look like this:

```no-highlight
{
    ... ,

    "person": {
        "name": "Adam",
        "surname": "Smith",
        "age": 25

    }
    ...
}
```

If there are no root objects, the server creates them, too. For example, if the database does not have a `"person1"` key but the user set the value `{"id1": 12, "id2": 14}` for the key `["person1", "inside1", "inside2"]`, then the database will have the following structure:

```no-highlight
{
    ... ,
    "person1": {
        "inside1": {
            "inside2" : {
                "id1": 12,
                "id2": 14
            }
        }
    },
    ...
}
```

The deletion of objects follows the same rules. If a user deletes the object above by the key `["person1", "inside1", "inside2]`, then only `"inside2"`  will be deleted, not `"inside1"` or `"person1"`. See the example below:

```no-highlight
{
    ... ,
    "person1": {
        "inside1": { }
    }

    ...
}
```