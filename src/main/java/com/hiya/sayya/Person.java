package com.hiya.sayya;


import lombok.NonNull;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

@Value
class Person {
    @NonNull String name;
    @NonNull String phoneNumber;
    private final Set<Interest> interests = new HashSet<>();

    void addInterest(@NonNull Interest got) {
        interests.add(got);
    }

    boolean isInterestedIn(@NonNull Interest got) {
        return interests.contains(got);
    }

    boolean sharesInterestsWith(@NonNull final Person other) {
        return other.getInterests().stream()
                .filter(interests::contains)
                .collect(Collectors.toSet())
                .isEmpty();
    }
}
