# README 

## Ideas 

This uses Spring's powerful `Trigger` interface to build a simple scheduling system that _triggers_ when any of the points in time from  a series of `Date` objects comes due. 

I'm imaginging how this might work with the `twitter-service` module, where I want to tweet all the things that need to be tweeted at certain times. But rather than constantly reading from the SQL database, 
a smart scheduler could ensure that we only do work when it's time to do work. This implementation communicates that work needs to be done by publishing `ApplicationEvent`s. It's also possible for the client that it
wants new work done (and that the old list of scheduled dates is no longer valid) with `ApplicationEvent` instances. 

We can use these events to invalidate old state. So, suppose we get a new `ScheduledTweet`. Here, we could simply publish an event with all the existing, unfinished dates _and_ whatever new ones have been added. This will have the effect of cancelling any existing work and enqueing the new stuff. If the existing stuff to be done finishes, and there  are no more callbacks to trigger, then nothing gets done and that's OK. 

## To Do: 

 * tests 
 * move this to Maven Central so that it's more accessible?  
 * integrate this with `twitter-service`, using the events to manipulate the schedule.