package org.idealist.lexer;

public sealed interface Token permits Token.Trait, Token.Do, Token.End, Token.Val, Token.Var, Token.Name {
    // No arguments

    record Trait() implements Token {

    }

    record Do() implements Token {

    }

    record End() implements Token {

    }

    record Val() implements Token {

    }

    record Var() implements Token {

    }

    // With Arguments

    record Name(String name) implements Token {

    }
}
