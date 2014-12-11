package com.itai.spellcheck.languageTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.itai.spellcheck.common.Pair;
import com.itai.spellcheck.common.SpellChecker;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

/**
 * @author Itai Peleg
 *         Created on 10/12/2014.
 */
public class LanguageTool implements SpellChecker {
    JLanguageTool langTool;

    public LanguageTool() throws Exception {
        langTool = new JLanguageTool(new AmericanEnglish());
        langTool.activateDefaultFalseFriendRules();
        List<String> disabledRule = new ArrayList<String>();
        for (Rule rule : langTool.getAllRules()) {
//            System.out.println(rule.getId());
            if (!rule.getId().equals("MORFOLOGIK_RULE_EN_US")) {
                disabledRule.add(rule.getId());
            }
        }
        langTool.disableRules(disabledRule);
    }

    public Pair<String, String> spellCheckOneToken(String oneToken) throws Exception {
        long start = System.nanoTime();
        List<RuleMatch> matches = langTool.check(oneToken);
        Pair<String, String> result = null;
        for (RuleMatch match : matches) {
            result = new Pair<>(oneToken.substring(match.getFromPos(), match.getToPos()),
                    match.getSuggestedReplacements().get(0));
        }
        System.out.printf("spellCheck took %dms, %s\n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() -
                start), result);
        return result;
    }

    public List<Pair<String, String>> spellCheck(String input) throws Exception {
        long start = System.nanoTime();
        List<Pair<String, String>> result = new ArrayList<>();
        List<RuleMatch> matches = langTool.check(input);
        for (RuleMatch match : matches) {
            result.add(new Pair<>(input.substring(match.getFromPos(), match.getToPos()),
                    match.getSuggestedReplacements().get(0)));
        }
        System.out.printf("spellCheck took %dms, %s\n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start),
                result);
        return result;
    }


}
