/**
 * Demonstrates a possible race condition in older Java versions due to 
 * non-atomic writes of 64-bit variables (long and double).
 *
 * <p>Prior to the JSR-133 revision of the Java Memory Model (JMM), writes to 
 * 64-bit variables were not guaranteed to be atomic on some architectures. 
 * This could lead to situations where a read operation observes a "split" 
 * value, containing parts of two different writes.</p>
 *
 * <p>This program starts multiple writer and reader threads. The writers 
 * continuously assign two distinct 64-bit values to a shared long variable, 
 * while the readers check if an inconsistent value appears.</p>
 *
 * <p>If an inconsistent value is detected, it will be printed to the console, 
 * indicating that the issue occurred.</p>
 *
 * <p>Reference: JSR-133: Java Memory Model and Thread Specification (Community Review Draft).</p>
 *
 * @author Giovanni Leopoldo Rozza
 * @version 1.0
 * @Date 19.02.2025
 */

import java.io.IOException;

public class RaceConditionDemo {
    private static long sharedCounter = 0;
    private static volatile boolean running = true;
    
    static class WriterThread extends Thread {
        @Override
        public void run() {
            while (running) {
                // Writing a value that must have its 64 bits consistent
                sharedCounter = 0x12345678ABCD0000L;
                // Immediately writing another value
                sharedCounter = 0x1111111111111111L;
            }
        }
    }
    
    static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (running) {
                long value = sharedCounter;
		// If we read a value that is neither 0x12345678ABCD0000L nor 0x1111111111111111L,
		// we have found an inconsistency
                if (value != 0x12345678ABCD0000L && value != 0x1111111111111111L) {
                	System.out.println(String.format("Detected inconsistent read: 0x%02X", value));                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Creating multiple threads to increase the chance of a race condition
        Thread[] writers = new Thread[2];
        Thread[] readers = new Thread[2];
        
        for (int i = 0; i < 2; i++) {
            writers[i] = new WriterThread();
            readers[i] = new ReaderThread();
            writers[i].start();
            readers[i].start();
        }
        
        // Waiting for user input to stop
        System.out.println("Press ENTER to terminate test...");
        try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Signaling the threads to stop
        running = false;
        
        // Wait threads terminate
        for (int i = 0; i < 2; i++) {
            writers[i].join();
            readers[i].join();
        }
        
        System.out.println("Test concluded");
    }
}
