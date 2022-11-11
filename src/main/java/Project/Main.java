package Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

    	
    	Instant start = Instant.now();
    	
        ExecutorService service = Executors.newFixedThreadPool(4);

        final int NUM_PRODUCERS = 3; // ! TODO : Ne soyez pas bête comme moi et mettez le bon nombre ici !
        AtomicInteger poisonCount = new AtomicInteger(0);

        BlockingQueue queue = new LinkedBlockingQueue();

        BlockingQueue outputQueue = new LinkedBlockingQueue();

        String database = "1000000";
        Path path = Paths.get("data/"+database+"/France.csv");
        Path path2 = Paths.get("data/"+database+"/Italy.csv");
        Path path3 = Paths.get("data/"+database+"/Spain.csv");

        service.execute(new Producer(path, queue, Country.FRANCE));
        service.execute(new Producer(path3, queue, Country.SPAIN));
        service.execute(new Producer(path2, queue, Country.ITALY));
        Future<String> future = service.submit(new Consumer(queue, poisonCount, NUM_PRODUCERS,outputQueue));

        //Finish
        shutdownAndAwaitTermination(service);


        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis()+" milliseconds");
      
        String str = future.get();
        System.out.println(str);
    }


    static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }


}
