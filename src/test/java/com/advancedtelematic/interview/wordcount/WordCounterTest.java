package com.advancedtelematic.interview.wordcount;

import com.advancedtelematic.interview.wordcount.counter.ParallelWordCounter;
import com.advancedtelematic.interview.wordcount.counter.SerialWordCounter;
import com.advancedtelematic.interview.wordcount.reader.CharacterReader;
import com.advancedtelematic.interview.wordcount.reader.FastCharacterReaderImpl;
import com.advancedtelematic.interview.wordcount.reader.SlowCharacterReaderImpl;
import com.google.common.collect.Ordering;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordCounterTest {

    public static final String CONTENT = "How much wood would a woodchuck chuck, if a woodchuck would chuck wood? \n" +
            "A woodchuck would chuck as much wood as a woodchuck could chuck if a woodchuck could chuck wood.";

    @Test
    public void testSerialCountsMatch() {
        CharacterReader fastReader = new FastCharacterReaderImpl(CONTENT);
        SerialWordCounter counter = new SerialWordCounter();

        Map<String, Integer> counts = counter.countWords(fastReader);

        assertTrue(Ordering.natural().reverse().isOrdered(counts.values()));
        assertKeyOrderPerCount(counts);

        assertEquals(counts.get("a"), 5);
        assertEquals(counts.get("chuck"), 5);
        assertEquals(counts.get("woodchuck"), 5);
        assertEquals(counts.get("wood"), 4);
        assertEquals(counts.get("would"), 3);
        assertEquals(counts.get("as"), 2);
        assertEquals(counts.get("could"), 2);
        assertEquals(counts.get("if"), 2);
        assertEquals(counts.get("much"), 2);
        assertEquals(counts.get("how"), 1);
    }

    @Test
    public void testParallelCountsMatch() {
        ParallelWordCounter counter = new ParallelWordCounter();
        CharacterReader[] readers = {new FastCharacterReaderImpl(CONTENT), new FastCharacterReaderImpl(CONTENT)};
        int nReaders = readers.length;

        Map<String, Integer> counts = counter.countWords(readers);

        assertTrue(Ordering.natural().reverse().isOrdered(counts.values()));
        assertKeyOrderPerCount(counts);

        assertEquals(nReaders * 5, counts.get("a"));
        assertEquals(nReaders * 5, counts.get("chuck"));
        assertEquals(nReaders * 5, counts.get("woodchuck"));
        assertEquals(nReaders * 4, counts.get("wood"));
        assertEquals(nReaders * 3, counts.get("would"));
        assertEquals(nReaders * 2, counts.get("as"));
        assertEquals(nReaders * 2, counts.get("could"));
        assertEquals(nReaders * 2, counts.get("if"));
        assertEquals(nReaders * 2, counts.get("much"));
        assertEquals(nReaders * 1, counts.get("how"));
    }

    @Test
    public void testLongFastParallelCountsProperly() {
        ParallelWordCounter counter = new ParallelWordCounter();
        CharacterReader[] readers = {new FastCharacterReaderImpl(), new FastCharacterReaderImpl()};

        Map<String, Integer> counts = counter.countWords(readers);

        assertTrue(Ordering.natural().reverse().isOrdered(counts.values()));
        assertKeyOrderPerCount(counts);
    }

    @Test
    @Disabled
    public void testSlowParallelCountsProperly() {
        ParallelWordCounter counter = new ParallelWordCounter();
        CharacterReader[] readers = {new SlowCharacterReaderImpl(), new SlowCharacterReaderImpl()};

        Map<String, Integer> counts = counter.countWords(readers);

        assertTrue(Ordering.natural().reverse().isOrdered(counts.values()));
        assertKeyOrderPerCount(counts);
    }

    private void assertKeyOrderPerCount(Map<String, Integer> counts) {
        for (Integer count : new HashSet<>(counts.values())) {
            List<String> effectiveEntryList = counts.entrySet().stream().filter(e -> e.getValue().compareTo(count) == 0).map(Map.Entry::getKey).collect(Collectors.toUnmodifiableList());
            System.out.println(String.format("Effective entries with count %s: %s", count, effectiveEntryList));
            assertTrue(Ordering.natural().isOrdered(effectiveEntryList));
        }
    }
}
