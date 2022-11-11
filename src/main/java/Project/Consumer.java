package Project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Consumer implements Callable<String> {


    private AtomicInteger poisonCount; // Compte de poison pills
    private int maxPoison; // Nombre max de poison pills : Nombre de producer
    BlockingQueue<Person> queue;
    long oldestDate = 0;
    List<Person> personList;
    List<Chain> chainList;
    BlockingQueue<List<Chain>> outputQueue;
    int chainCount = 0;
    int idCounter = 0;
    HashMap<Integer, Person> personHashMap;


    public Consumer(BlockingQueue<Person> queue, AtomicInteger poisonCount,
        final int maxPoison, BlockingQueue<List<Chain>> outputQueue) {
        this.queue = queue;
        this.maxPoison = maxPoison;
        this.poisonCount = poisonCount;
        this.personList = new ArrayList<Person>();
        this.chainList = new ArrayList<Chain>();
        this.outputQueue = outputQueue;
        this.personHashMap = new HashMap<Integer, Person>();
    }

    public String call() {

        while (true) {
            Person receivedPerson = this.personHashMap.get(idCounter);
            while (receivedPerson == null && this.maxPoison != poisonCount.get()) {
                receivedPerson = this.queue.poll();
            }
            
            if (this.maxPoison == poisonCount.get() && this.personHashMap.isEmpty()) {
                break;
            }
            if (receivedPerson.equals(Producer.POISON_PILL_PERSON)) {
                poisonCount.incrementAndGet();
            } else {

                // TODO: Process the person
                if (idCounter == receivedPerson.getId()) {
                    processPerson(receivedPerson);
                    
                    
                    if (Main.DEBUG) {
                        System.out.println(receivedPerson);
                    }

                    this.personHashMap.remove(idCounter);
                    idCounter++;
					if(idCounter%1000==0) System.out.println(idCounter);

                } else {
                    this.personHashMap.put(receivedPerson.getId(), receivedPerson);
                }
            }

        }

        // On a rempli la chainList tout le long du programme
        List<Chain> top = chainList.stream().sorted(Comparator.reverseOrder()).limit(3)
            .filter(el -> el.getChainPoints() != 0).collect(Collectors.toList());

        return generateOutputStringFromList(top);
    }

    public void processPerson(Person newPerson) {
        int chainId = -1;
        long tmpOldestDate = oldestDate;
        oldestDate = newPerson.getMillisContamination();
        

        personList = personList.stream().filter(listPerson -> {
            listPerson.updatePointsWithMillis(oldestDate);
            if (listPerson.getPoints() == 0) {
                return false;
            }
            
            return true;
        }).collect(Collectors.toList());

        Person personInChain = personList.stream()
            .filter(listPerson -> (newPerson.getSourceId() == listPerson.getId()
                || listPerson.getSourceId() == listPerson.getId())).findAny()
            .orElse(null);

        chainId = personInChain == null ? -1 : personInChain.getChain();

        if (chainId == -1) {
            chainId = chainCount;
            chainList.add(new Chain(chainId, newPerson.getCountry(), newPerson.getId(),
                newPerson.getMillisContamination()));
            chainCount++;


        }

        newPerson.setChain(chainId);
        newPerson.updatePointsWithMillis(oldestDate);

        personList.add(newPerson);

        // TODO : On met à jour les scores des chaines
        for (Chain chain : chainList) {
            chain.resetPoints();
            chain.setOldestContamination(0);
        }

        for (Person p : personList) {
            Chain chain = chainList.get(p.getChain());
            chain.addPoints(p.getPoints());

            // Si le cas de contamination est plus vieux que le plus vieux de la chaine
            if (chain.getOldestContamination() > p.getMillisContamination()
                || chain.getOldestContamination() == 0) {
                chain.setOldestContamination(p.getMillisContamination());
                chain.setOriginCountry(p.getCountry());
                chain.setChainRootPersonId(p.getId());
            }
            chainList.set(p.getChain(), chain);
        }
        if (Main.DEBUG) {
            System.out.println("----------------");
        }
        List<Chain> top = chainList.stream().sorted(Comparator.reverseOrder()).limit(3)
            .filter(el -> el.getChainPoints() != 0).collect(Collectors.toList());
        if (Main.DEBUG) {
            for (Chain c : top) {
                System.out.println(c);
            }
        }
        //
        // utputQueue.add(top);

    }

    public String generateOutputStringFromList(List<Chain> top) {
        String ret = "";
        for (Chain c : top) {
            ret += c.toString();
        }
        return ret;
    }


}
