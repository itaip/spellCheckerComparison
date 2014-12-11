package com.itai.spellcheck.languageTool;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.itai.spellcheck.common.Pair;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Itai Peleg
 *         Created on 10/12/2014.
 */
public class LanguageToolTest {

    @Test
    public void spellCheck() throws Exception {
        long a = System.nanoTime();
        LanguageTool languageTool = new LanguageTool();
        Pair<String, String> result = languageTool.spellCheckOneToken("nikin");
        assertEquals("nikon", result.getRight().toLowerCase());

        result = languageTool.spellCheckOneToken("sanon");
        assertEquals("canon", result.getRight().toLowerCase());

        List<Pair<String, String>> pairs = languageTool.spellCheck("nikin sanon");
        assertFalse(pairs.isEmpty());
        assertEquals(2, pairs.size());

        result = languageTool.spellCheckOneToken("sure");
        assertNull(result);
        System.out.printf("total = %dms\n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - a));

    }
}
