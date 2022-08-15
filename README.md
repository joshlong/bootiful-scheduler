# README 

## Ideas 

This uses Spring's powerful `Trigger` interface to build a simple scheduling system that _triggers_ when any of the points in time from  a series of `Instant` objects comes due.

## Usage 

Add the dependency (a Spring Boot __starter_) to your build  

```xml 
<dependency>
    <groupId>com.joshlong</groupId>
    <artifactId>scheduling-starter</artifactId>
    <version>0.1</version>
</dependency>
```

There's now an engine running alongside your code. If you want dispatches at two different `Instant`s in the future, you may either: 

* inject the `SchedulingService` and call `schedule(Collection\<Instant\> instants)` method
* publish an event of type `ScheduleRefreshEvent` containing zero or more `Instant` references 

Either one of these will have the effect of clearing whatever outstanding callbacks were planned and installing the new callbacks. That is, the effect is _not_ additive. If you had two callbacks for some point in the future but want to add three more, you'll have to resubmit all five. 

Clients can listen for a `ScheduleEvent` in the normal ways:  
* implement `ApplicationListener\<ScheduleEvent\>`
* use `@EventListener` 

If you use `@EventListener`, be sure to define a parameter of type `ScheduleEvent` for your handler method. `ScheduleEvent#getSource()` returns the `Instant` that you specified corresponding to the time (roughly) of this callback. 


## Warning 
This code is provided as-is. As far as I know, it works! But don't rely on something like this for anything strictly realtime



