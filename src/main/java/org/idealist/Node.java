package org.idealist;

import java.util.List;

public sealed interface Node permits Node.Trait, Node.Member.Property {
    record Trait(String name, List<Member.Property> members) implements Node {
        public Trait(String name) {
            this(name, List.of());
        }
    }

    sealed interface Member permits Member.Property {
        record Property(String name, boolean mutable) implements Node, Member {
            public Property(String name) {
                this(name, false);
            }
        }
    }


}
