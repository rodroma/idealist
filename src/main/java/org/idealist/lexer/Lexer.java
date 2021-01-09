package org.idealist.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public List<Token> lex(CharSequence input) {
        return lex(input, Level.INFO);
    }

    public List<Token> lex(CharSequence input, Level logLevel) {
        return new OngoingLexing(input, logLevel).lex();
    }

    private static class OngoingLexing {
        private CharSequence input;
        private final Logger logger;
        private final List<Token> tokens;
        private final List<SingleTokenLexer> lexers;

        private OngoingLexing(CharSequence input, Level logLevel) {
            this.input = input;
            this.logger = Logger.getLogger("OngoingLexing");
            this.logger.setLevel(logLevel);
            this.tokens = new ArrayList<>();
            this.lexers = List.of(
                new NullaryToken("trait", new Token.Trait()),
                new NullaryToken("do", new Token.Do()),
                new NullaryToken("end", new Token.End()),
                new NullaryToken("val", new Token.Val()),
                new NullaryToken("var", new Token.Var()),
                new StatefulToken("[_A-Za-z][_A-Za-z0-9]*", matcher -> new Token.Name(matcher.group()))
            );
        }

        private List<Token> lex() {
            while (!isExhausted()) {
                tokens.add(next());
            }

            return tokens;
        }

        private Token next() {
            consumeAllLeadingWhitespace();

            for (var lexer :lexers){
                final var matcher = lexer.matcher(input);

                if (matcher.find()) {
                    final var token = lexer.extract(matcher);
                    this.input = matcher.replaceFirst("");
                    return token;
                }

                logger.info(() -> lexer + " did not match " + input);
            }

            throw new RuntimeException("Expected EOF, got " + input);
        }

        private boolean isExhausted() {
            return input.length() == 0;
        }

        private void consumeAllLeadingWhitespace() {
            while (consumeSingleIfWhitespace()) {}
        }

        private boolean consumeSingleIfWhitespace() {
            final var ch = this.input.charAt(0);
            if (Character.isWhitespace(ch)) {
                input = input.subSequence(1, input.length());
                return true;
            }
            return false;
        }
    }

    private static abstract class SingleTokenLexer {
        protected final String pattern;
        private final Pattern actualPattern;

        protected SingleTokenLexer(String pattern) {
            this.pattern = pattern;
            this.actualPattern = Pattern.compile("^" + pattern);
        }

        public Matcher matcher(CharSequence input) {
            return actualPattern.matcher(input);
        }

        public abstract Token extract(Matcher matcher);
    }

    private static class StatefulToken extends SingleTokenLexer {
        private final Function<Matcher, Token> extractor;

        public StatefulToken(String pattern, Function<Matcher, Token> extractor) {
            super(pattern);
            this.extractor = extractor;
        }

        @Override public Token extract(Matcher matcher) {
            return extractor.apply(matcher);
        }

        @Override public String toString() {
            return "StatefulToken(expecting = " + pattern + ")";
        }
    }

    private static class NullaryToken extends SingleTokenLexer {
        private final Token token;

        public NullaryToken(String pattern, Token token) {
            super(pattern);
            this.token = token;
        }

        @Override public Token extract(Matcher matcher) {
            return token;
        }

        @Override public String toString() {
            return "NullaryToken(expecting = " + token.getClass().getSimpleName() + ")";
        }
    }
}
