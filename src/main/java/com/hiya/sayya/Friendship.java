package com.hiya.sayya;

import lombok.NonNull;
import lombok.Value;

@Value
class Friendship {
    @NonNull Person first;
    @NonNull Person second;

    boolean hasPerson(final Person person) {
        return first.equals(person) || second.equals(person);
    }

    boolean isEquivalentTo(final @NonNull Friendship other) {
        return this.equals(other) || (other.hasPerson(first) && other.hasPerson(second));
    }
}
