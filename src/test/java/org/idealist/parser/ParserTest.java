package org.idealist.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.idealist.Node;
import org.idealist.lexer.Token;
import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void should_parse_empty_trait() {
        final List<Token> tokens = List.of(
                new Token.Trait(),
                new Token.Name("Any"),
                new Token.Do(),
                new Token.End()
        );

        assertEquals(new Node.Trait("Any"), parseTrait(tokens));
    }

    @Test
    public void should_parse_traits_with_members() {
        final List<Token> tokens = List.of(
                new Token.Trait(),
                new Token.Name("Option"),
                new Token.Do(),
                new Token.Val(),
                new Token.Name("value"),
                new Token.End()
        );

        final var expected = new Node.Trait(
                "Option",
                List.of(
                        new Node.Member.Property("value", false)));

        assertEquals(expected, parseTrait(tokens));
    }

    @Test
    public void should_parse_considering_mutability() {
        final List<Token> tokens = List.of(
                new Token.Trait(),
                new Token.Name("WithMutability"),
                new Token.Do(),
                new Token.Val(),
                new Token.Name("immutable"),
                new Token.Var(),
                new Token.Name("mutable"),
                new Token.End()
        );

        final var expected = new Node.Trait(
                "WithMutability",
                List.of(
                        new Node.Member.Property("immutable", false),
                        new Node.Member.Property("mutable", true)));

        assertEquals(expected, parseTrait(tokens));
    }

    // Helpers
    private Node.Trait parseTrait(List<Token> tokens) {
        return new Parser(tokens).parseTrait();
    }
}
