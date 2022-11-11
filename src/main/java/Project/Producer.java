package Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class Producer implements Runnable {

    // Cette donnée est la poison pill, on suppose qu'elle n'existe pas dans les données
    public static final Person POISON_PILL_PERSON = new Person(-1, 0, 0, Country.FRANCE);

    private BlockingQueue<Person> queue;
    private boolean hasStarted = false;
    private Path filePath;
    private Country country;

    public Producer(Path filePath, BlockingQueue<Person> queue, Country country) {
        this.queue = queue;
        this.filePath = filePath;
        this.country = country;
    }

    @Override
    public void run() {
        this.hasStarted = true;
        readFileAndFillQueue();
        queue.add(POISON_PILL_PERSON);
    }


    private void readFileAndFillQueue() {
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            Stream<String> stream = br.lines();

            // for each line in the stream

            stream.forEach((line) -> {

                Person parsedPerson =parseStringToPerson(line);
                queue.add(parsedPerson);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Person parseStringToPerson(String receivedMsg) {
        final String delimiter = ",";
        final String unknownContaminedBy = "unknown"; // Chaine de caractere utilisée pour désigner un contaminer inconnu
        Person ret = new Person(0, 0, 0,
            this.country);
        try {
            String[] token = receivedMsg.split(delimiter);

            int id = Integer.valueOf(token[0].trim());
            // Le timestamp dans les données est en s, on va le convertir en ms
            double epochSec = Double.valueOf(token[4]);
            long millis = (long) (epochSec * 1000);

            // La ligne en dessous est bizarre ? Lisez ça https://waytolearnx.com/2020/03/operateur-ternaire-en-java.html
            String contaminatedByStrClean = token[5].trim();
            int contaminatedBy = contaminatedByStrClean.equals(unknownContaminedBy) ? -1
                : Integer.valueOf(contaminatedByStrClean);

            ret.setId(id);
            ret.setMillisContamination(millis);
            ret.setSourceId(contaminatedBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }
}
