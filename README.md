# Problem Statement
Design and implement a thread-safe class that allows registration of callback methods that are executed after a user
specified time interval in seconds has elapsed.

## Solution Approach
1. Design a callback record which accepts a `Runnable` and the time at which the callback is scheduled. It should
implement a `Comparable` to help maintain ascending order.
2. Design a `DeferredCallbackExecutor` that maintains order of the callbacks in a priority queue. Registering callbacks
is done via a method that synchronizes over the priority queue instance. A background ExecutorThread is launched at the
first time a callback is registered.
3. The background thread runs an infinite loop (till shutdown) that first waits for the priority queue to have some
callbacks, then waits for the specific duration between current time and scheduled time.
4. If executor is woken up by another incoming callback, it recomputes the time for it should wait, and goes back to 
waiting.
5. On the other hand, if executor computes that it does not need to wait any longer, the executor thread creates another
thread on which the callback runs.

## Sample output
```
Creating 9 callbacks
Scheduling callback at 1688309914936
Scheduling callback at 1688309915683
Scheduling callback at 1688309911448
Scheduling callback at 1688309909390
Scheduling callback at 1688309907190
Scheduling callback at 1688309906914
Scheduling callback at 1688309901571
Scheduling callback at 1688309912958
Scheduling callback at 1688309910670
Registered all callbacks, waiting for successful execution
Number of callbacks run : 0
Number of callbacks run : 0
Number of callbacks run : 0
Callback scheduled at 1688309901571 is running at 1688309901571
Running callback
Number of callbacks run : 1
Number of callbacks run : 1
Number of callbacks run : 1
Callback scheduled at 1688309906914 is running at 1688309906920
Running callback
Callback scheduled at 1688309907190 is running at 1688309907202
Running callback
Number of callbacks run : 3
Callback scheduled at 1688309909390 is running at 1688309909391
Running callback
Number of callbacks run : 4
Callback scheduled at 1688309910670 is running at 1688309910670
Running callback
Callback scheduled at 1688309911448 is running at 1688309911449
Running callback
Number of callbacks run : 6
Callback scheduled at 1688309912958 is running at 1688309912959
Running callback
Number of callbacks run : 7
Callback scheduled at 1688309914936 is running at 1688309914937
Running callback
Callback scheduled at 1688309915683 is running at 1688309915683
Running callback
Number of callbacks run: 9
Shutting down executor
```