
# Functional Requirements
- Simple logging library 
  - with different configurable outputs appenders like (file, console, external log provider like New Relic / Datadog/ Splunk)
  - set the log level limit 
- set current timestamp 
- show levels for the log 
- show class name 


## Entities 
- LogLevel (TRACE, DEBUG, INFO, WARN, ERROR)
- CustomLogger Interface (Console appender, file appender, external agent) 
- LogService 



## Improvements
- Async write 


Questions : What are the fields to be present in logs 
Answer : timestamp, log level, message, class, trace-id

## Logging Logic
- We will producer and consumer pattern, log.info("message") will produce and send message to queue/channel
- Multiple consumers listening to various channels

### Consumer Side Logic

- consumer will have list of consumers 
- consumers will have list of queues


QueueMessage will contain this message 
```json
{
  "level",
  "message",
  "traceId"
}
```

onMessage(queueMessage)

we will find class name from channel-id
from thread-id we can know thread name, traceId and request context details



## Optimize the file appender 
two options ?
1. buffer per consumer
2. single buffer 

we will choose single buffer. 
If we choose buffer per consumer then, resource allocation is not wise. Some class may be adding a lot of logs than other.
When we append logs from a buffer to file, logs of a class will be together and logs may be out of order.

So, we will use Single Buffer.


So, the whole architecture, producer => queues per class => consumer per queue, => single buffer for all  consumer => bulk update to file when buffer is full or after each fixed interval.

## How to decide when to write from buffer to file 
1. when capacity is full
2. after fixed interval 

We will use policy based buffer, so whichever condition is true, we write to file.



Question : What is the drawback of this ?
Ansswer : In case the system / application dies, we will lose logs that are in buffer and not written to file.
