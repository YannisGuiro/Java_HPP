package Benchmarking;

import Project.Consumer;
import Project.Country;
import Project.Producer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkMain {

    @Param({"50", "100", "150", "200", "250", "300", "350", "400", "450", "500", "550", "600", "650", "700", "750", "800", "850", "900", "950", "1000", "25000"})
    String datasetSize;
    


    @Benchmark
    public String testForLoopMeanFixedSize() throws ExecutionException, InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(4);

        final int NUM_PRODUCERS = 3; // ! TODO : Ne soyez pas bête comme moi et mettez le bon nombre ici !
        AtomicInteger poisonCount = new AtomicInteger(0);
        AtomicLong oldestDate = new AtomicLong(0);
        AtomicInteger chainCount = new AtomicInteger(0);

        BlockingQueue queue = new LinkedBlockingQueue();

        BlockingQueue outputQueue = new LinkedBlockingQueue();

        Path path = Paths.get("data/benchmark/"+datasetSize+"/France.csv");
        Path path2 = Paths.get("data/benchmark/"+datasetSize+"/Italy.csv");
        Path path3 = Paths.get("data/benchmark/"+datasetSize+"/Spain.csv");

        service.execute(new Producer(path, queue, Country.FRANCE));
        service.execute(new Producer(path3, queue, Country.SPAIN));
        service.execute(new Producer(path2, queue, Country.ITALY));
        
        //Future<String> future1 = service.submit(new Consumer(queue, poisonCount, NUM_PRODUCERS,outputQueue));
        Future<String> future = service.submit(new Consumer(queue, poisonCount, NUM_PRODUCERS,outputQueue));

        //Finish
        shutdownAndAwaitTermination(service);
        String str = future.get();
        return str;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(BenchmarkMain.class.getSimpleName())
            .build();

        new Runner(opt).run();
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
