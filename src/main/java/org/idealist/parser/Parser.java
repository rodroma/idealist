package org.idealist.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.idealist.Node;
import org.idealist.lexer.Token;

public class Parser {
    private List<Token> tokens;

    public Parser(List<Token> tokens) {
        // We create a defensive copy because this parser is stateful
        this.tokens = new ArrayList<>(tokens);
    }

    public Node.Trait parseTrait() {
        consume(Token.Trait.class);
        final var name = consume(Token.Name.class);
        consume(Token.Do.class);
        final var members = parseTraitMembers();
        consume(Token.End.class);

        return new Node.Trait(name.name(), members);
    }

    private List<Node.Member.Property> parseTraitMembers() {
        List<Node.Member.Property> members = new ArrayList<>();

        while (true) {
            final var member = parseSingleTraitMember();
            if (member.isEmpty()) {
                break;
            }
            members.add(member.get());
        }

        return members;
    }

    private Optional<Node.Member.Property> parseSingleTraitMember() {
        return peek()
            .filter(token -> token instanceof Token.Val || token instanceof Token.Var)
            .map(token -> {
                final var mutable = token instanceof Token.Var;

                consumeIgnoring(1);
                final var name = consume(Token.Name.class).name();

                return new Node.Member.Property(name, mutable);
            });
    }

    private boolean isExhausted() {
        return tokens.isEmpty();
    }

    private Optional<Token> peek() {
        return peek(Token.class);
    }

    private <T extends Token> Optional<T> peek(Class<T> tokenClass) {
        if (isExhausted()) return Optional.empty();

        try {
            return Optional.of(tokenClass.cast(tokens.get(0)));
        } catch (ClassCastException cce) {
            return Optional.empty();
        }
    }

    private <T extends Token> T consume(Class<T> tokenClass) {
        if (isExhausted())
            throw new IllegalStateException("tried to consume " + tokenClass.getSimpleName() + " but found end");

        return tokenClass.cast(tokens.remove(0));
    }

    private void consumeIgnoring(int howMany) {
        if (howMany == 0) {
            throw new IllegalStateException("can't consume 0 tokens");
        }

        this.tokens = this.tokens.subList(howMany, this.tokens.size());
    }

    private <T, R> Function<T, R> constant(R value) {
        return ignored -> value;
    }
}
