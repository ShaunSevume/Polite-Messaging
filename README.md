Polite Messaging (PM) is a peer-to-peer, asynchronous messaging system in which peers store and exchange messages.

PM runs over TCP, using a client and server on TCP port 20111.

The main purpose of PM is to store and exchange messages. Each message consists of four or more lines. These lines are interpreted as headers until the Contents header has been found. All lines after that are interpreted as body. The following headers appear by default in all messages:

Message-id: SHA-256 hash
This is always the first header. The hash is the SHA-256 sum of the rest of the headers and the body of the message.

Time-sent: time
The time is when the message was created.

From: person
This identifies who sent the message.

Contents: number
The number gives how many lines of the body follow.

Users can add their own custom headers to messages.

The intent of PM is that messages flood across the network so that they can reach all participants and so that the network will still operate even if some nodes are unavailable. Messages are stored in a txt file by the server.

Communication in PM consists of a number of requests each of which may have a response. The requests that can be are listed below:

1) PROTOCOL?

Both peers  send a protocol request as the first request they send. A
protocol request is a single line with three parts:

PROTOCOL? version identifier

Here, "version" is a positive integer giving the protocol version. This is the specification of version 1. "identifier" is a string that identifies the peer. There is no response to a protocol request.


2) TIME?

A time request is a single line:

TIME?

The response is a single line with two parts:

NOW time

"time" is the current time at the peer.


3) LIST?

A list request is one or more lines. The first line has three parts:

LIST? since headers

Here, "since" is any time in the past. Times in the future cannot be requested. "headers" is an integer which MUST be 0 or more. This gives the number of following lines which contain headers.

The response is one or more lines. The first line has two parts:

MESSAGES count

The responding peer finds every message it has stored with:
    1. A Time-sent header that is greater than or equal to since.
    2. All of the headers that are given in the request

"count" is the number of messages that it has found. It then outputs the hash
from the Message-id header of each of the messages.


4) GET?

A get request is one line. It has three parts:

GET? SHA-256 hash

Here "hash" must be an SHA-256 sum.

There are two possible responses. A peer can respond with a single line:

SORRY

or it can respond with multiple lines, the first  is:

FOUND

then it must send the message with the requested message ID.


5) BYE!

A bye request is a single line:

BYE!

There is no response but both peers must close the socket.

