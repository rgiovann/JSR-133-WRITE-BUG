 ##### Demonstrates a possible race condition in older Java versions due to 
 ##### non-atomic writes of 64-bit variables (long and double).

Prior to the JSR-133 revision of the Java Memory Model (JMM), writes to 
 64-bit variables were not guaranteed to be atomic on some architectures. 
 This could lead to situations where a read operation observes a "split" 
 value, containing parts of two different writes.
 
 This program starts multiple writer and reader threads. The writers 
 continuously assign two distinct 64-bit values to a shared long variable, 
 while the readers check if an inconsistent value appears.
 
If an inconsistent value is detected, it will be printed to the console, 
indicating that the issue occurred.

Reference: JSR-133: Java Memory Model and Thread Specification (Community Review Draft).