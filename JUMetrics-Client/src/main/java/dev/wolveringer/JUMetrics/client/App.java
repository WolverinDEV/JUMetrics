package dev.wolveringer.JUMetrics.client;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	System.out.println("Sending metrics!");
       Metrics m = new Metrics("localhost", 1111);
       m.sendMetrics();
    }
}
