package il.ac.technion.cs.sd.book.library;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import il.ac.technion.cs.sd.book.ext.LineStorage;
import il.ac.technion.cs.sd.book.ext.LineStorageFactory;

import javax.inject.Inject;
import java.util.*;


public class StringStorage extends AbstractList<String> implements RandomAccess, Storage {
    private LineStorage lineStorage;
    private boolean sizeIsValid = false;
    private int size = 0;

    private static final String DELIMITER = ",";

    @AssistedInject
    public StringStorage(
            LineStorageFactory lineStorageFactory,
            @Assisted String fileName
    ) {
        this.lineStorage = lineStorageFactory.open(fileName);
        sizeIsValid = false;
    }

    @AssistedInject
    public StringStorage(
             LineStorageFactory lineStorageFactory,
             @Assisted("fileName") String fileName,
             @Assisted("xmlString") String xmlString,
             @Assisted("xmlQuery") String xmlQuery,
             @Assisted("swapKeys") boolean swapKeys
    ) {
        this(lineStorageFactory, fileName);
        SortedMap<String, String> sortedMap = XMLParser.parseXMLToSortedMap(xmlString, xmlQuery, swapKeys);
        sortedMap.forEach((k, v) -> lineStorage.appendLine(String.join(DELIMITER, k,v)));
        sizeIsValid = false;
    }

    @Override
    public String get(int index) {
        try {
            return lineStorage.read(index);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try {
            if (sizeIsValid) {
                return size;
            } else {
                size = lineStorage.numberOfLines();
                sizeIsValid = true;
                return size;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String id0, String id1) {
        return getStringByIds(id0, id1).isPresent();
    }

    @Override
    public Optional<String> getStringByIds(String id0, String id1) {
        int keyFound = findIndexByTwoKeys(id0, id1);
        if (keyFound >= 0) {
            return Optional.of(get(keyFound));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<String> getAllStringsById(String id) {
        int keyFound = findIndexBySingleKey(id);
        LinkedList<String> sortedBySecondaryId = new LinkedList<>();
        if (keyFound >= 0) {
            for (int i = keyFound ; i >=0 ; --i) {
                String toAdd = get(i);
                if (toAdd.split(DELIMITER)[0].equals(id)) {
                    sortedBySecondaryId.addFirst(toAdd);
                } else {
                    break;
                }
            }
            for (int i = keyFound + 1 ; i < size() ; ++i) {
                String toAdd = get(i);
                if (toAdd.split(DELIMITER)[0].equals(id)) {
                    sortedBySecondaryId.addLast(toAdd);
                } else {
                    break;
                }
            }
            return sortedBySecondaryId;
        } else {
            return sortedBySecondaryId;
        }
    }

    private int findIndexByTwoKeys(String key0, String key1) {
        return findIndexByComparator(
                String.join(DELIMITER, key0, key1),
                Comparator.comparing((String s) -> s.split(DELIMITER)[0])
                        .thenComparing((String s)-> s.split(DELIMITER)[1])
        );
    }

    private int findIndexBySingleKey(String key) {
        return findIndexByComparator(
                key,
                Comparator.comparing((String s) -> s.split(DELIMITER)[0])
        );

    }

    private int findIndexByComparator(String key, Comparator comparator) {
        int keyFound;

        try {
            keyFound = Collections.binarySearch(this, key, comparator);
        } catch (RuntimeException e) {
            throw e;
        }

        return keyFound;
    }
}
