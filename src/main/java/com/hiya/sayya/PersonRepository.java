package com.hiya.sayya;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PersonRepository {
    private final Set<Friendship> friendships = Sets.newHashSet();
    private final List<Friendship> potential = Lists.newArrayList();
    private final List<Person> stored = Lists.newArrayList();

    void save(@NonNull final Person nick) {
        stored.add(nick);
    }

    boolean containsPersonWithName(final String nick) {
        return stored.parallelStream()
                .filter(p -> p.getName().equals(nick)).findFirst()
                .isPresent();
    }

    void addFriendship(@NonNull final Person nick, @NonNull final Person michael) {
        final Friendship friendship = new Friendship(nick, michael);
        potential.add(friendship);
    }

    void verifyFriendship(@NonNull final Person michael, @NonNull final Person nick) {
        final Optional<Friendship> requested = potential.parallelStream()
                .filter(friendship -> friendship.hasPerson(michael) && friendship.hasPerson(nick))
                .findFirst();
        if(requested.isPresent()) {
            final Friendship accepted = requested.get();
            friendships.add(accepted);
            potential.remove(accepted);
        }
    }

    Set<Friendship> getFriendships() { return Collections.unmodifiableSet(friendships); }

    void rejectFriendship(@NonNull final Person michael, @NonNull final Person nick) {
        final Optional<Friendship> requested = potential.parallelStream()
                .filter(friendship -> friendship.hasPerson(michael) && friendship.hasPerson(nick))
                .findFirst();
        if(requested.isPresent()) {
            potential.remove(requested.get());
        }
    }

    Optional<Person> findByPhoneNumber(final String phoneNumber) {
        return stored.parallelStream()
                .filter(p -> p.getPhoneNumber().equals(phoneNumber))
                .findFirst();
    }

    Set<Friendship> getFriendsOf(@NonNull final Person person) {
        return friendships.stream()
                .filter(f -> f.hasPerson(person))
                .collect(Collectors.toSet());
    }

    Set<Friendship> getFamiliarStrangersByInterest(@NonNull final Interest interest) {
        final Set<Person> withInterest = friendships.parallelStream()
                .flatMap(friendship -> Stream.of(friendship.getFirst(), friendship.getSecond()))
                .filter(person -> person.isInterestedIn(interest))
                .collect(Collectors.toSet());
        final Set<Friendship> possibleFriends = combinations(withInterest.toArray());
        return possibleFriends;
    }

    private Set<Friendship> combinations(final Object[] objects) {
        final Set<Friendship> result = Sets.newHashSet();
        for (int i =0; i < objects.length; i++) {
            for (int j = i + 1 ; j < objects.length; j++) {
                final Person one = (Person) objects[i];
                final Person two = (Person) objects[j];
                final Friendship f = new Friendship(one, two);
                if (!(friendships.parallelStream().anyMatch(friendship -> friendship.isEquivalentTo(f)))) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    Optional<Person> getFamiliarStrangerForPersonByInterest(Person toFindFor, Interest shared) {
        final Set<Friendship> possible = getFamiliarStrangersByInterest(shared);
        final Optional<Person> other = possible.stream()
                .filter(friendship -> friendship.hasPerson(toFindFor))
                .findAny()
                .flatMap(friendship -> Optional.of(friendship.getFirst().equals(toFindFor) ? friendship.getSecond() : friendship.getFirst()));
        return other;
    }
}
