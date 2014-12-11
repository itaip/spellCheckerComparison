package com.itai.spellcheck.common;

import java.util.List;

/**
 * @author Itai Peleg
 *         Created on 10/12/2014.
 */
public interface SpellChecker {
    Pair<String, String> spellCheckOneToken(String oneToken) throws Exception;
    List<Pair<String, String>> spellCheck(String input) throws Exception;
}
