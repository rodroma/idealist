package org.idealist.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.junit.jupiter.api.Test;

public class LexerTests {
    // Change level for debugging
    private final Level logLevel = Level.WARNING;

    @Test public void should_lex_nullary_tokens() {
        final Map<String, Token> cases = Map.of(
            "trait", new Token.Trait(),
            "do", new Token.Do(),
            "end", new Token.End(),
            "val", new Token.Val(),
            "var", new Token.Var()
        );

        cases.forEach(this::singleTokenTestCase);
    }

    @Test public void should_lex_single_character_names() {
        nameTestCase("a");
    }

    @Test public void should_lex_class_like_names() {
        nameTestCase("Iterable");
    }

    @Test public void should_lex_variable_like_names() {
        nameTestCase("some_var");
    }

    @Test public void should_lex_names_with_numbers() {
        nameTestCase("some_V4r1able_with_Numb3rs");
    }

    @Test public void should_lex_multiple_tokens() {
        final List<Token> expected = List.of(
            new Token.Trait(),
            new Token.Name("Any"),
            new Token.Do(),
            new Token.End()
        );

        assertEquals(expected, lex("trait Any do end"));
    }

    // Helpers
    private void nameTestCase(String name) {
        singleTokenTestCase(name, new Token.Name(name));
    }

    private void singleTokenTestCase(String given, Token expect) {
        assertEquals(expect, lex(given).get(0));
    }

    private List<Token> lex(String given) {
        return new Lexer().lex(given, logLevel);
    }
}
