package com.hiya.sayya;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

public class SayYaVerificationTest {
    @Test
    public void connectUsersWithSimilarInterests() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // And: An Interest Game of Thrones
        Interest got = new Interest("Game of Thrones");
        // And: Nick has an Interest in Game of Thrones
        nick.addInterest(got);
        // And: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: Nick and Michael have a Friendship
        repo.addFriendship(nick, michael);
        repo.verifyFriendship(michael, nick);
        // And: a Person named Rob with a phone number 555-234-6777
        Person rob = new Person("Rob", "555-234-6777");
        // And: Michael and Rob have a Friendship
        repo.addFriendship(michael, rob);
        repo.verifyFriendship(rob, michael);
        // And: Rob is also reading Game of Thrones
        rob.addInterest(got);
        // And: Nick and Rob are not friends
        // When: Nick wants to connect with a familiar stranger over a shared interest, Game of Thrones
        Optional<Person> randomFamiliarStranger = repo.getFamiliarStrangerForPersonByInterest(nick, got);
        // Then: The PersonRepository finds a match
        Assert.assertTrue(randomFamiliarStranger.isPresent());
        Assert.assertEquals(randomFamiliarStranger.get(), rob);
    }

    @Test
    public void storeAPerson() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // When: I save the Person to the PersonRepository
        repo.save(nick);
        // Then: The PersonRepository contains a Person Named Nick
        Assert.assertTrue(repo.containsPersonWithName("Nick"));
    }

    @Test
    public void queryAPersonByPhoneNumber () {
        // Given: I have a person named Nick with phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: repo exists
        PersonRepository repo = new PersonRepository();
        // And: I add nick to my PersonRepository
        repo.save(nick);
        // When: I findByPhoneNumber the PersonRepository for 516-847-4660
        Optional<Person> found = repo.findByPhoneNumber("516-847-4660");
        // Then: I get the Person named Nick with the phone number 516-847-4660
        Assert.assertEquals(nick, found.get());
    }

    @Test
    public void createFriendship() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // When: Nick adds Michael as a Friend
          repo.addFriendship(nick, michael);
        // And: Michael verifies they are friends
          repo.verifyFriendship(michael, nick);
        // Then: A Friendship is created between Nick and Michael
        Assert.assertTrue(repo.getFriendships().stream().filter(f -> f.hasPerson(michael))
                .findFirst().isPresent());
    }

    @Test
    public void friendshipCreationRejected() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: Given: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // When: Nick adds Michael as a Friend
        repo.addFriendship(nick, michael);
        // And: Michael rejects friendship
        repo.rejectFriendship(michael, nick);
        // Then: A Friendship is created between Nick and Michael
        Assert.assertFalse(repo.getFriendships().contains(new Friendship(nick, michael)));
    }

    @Test
    public void canGetFriendshipsByPerson() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: Given: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // When: Nick adds Michael as a Friend
        repo.addFriendship(nick, michael);
        // And: Michael verifies they are friends
        repo.verifyFriendship(michael, nick);
        // When: I query the PersonRepository for Nick’s Friendships
        Set<Friendship> friends = repo.getFriendsOf(nick);
        // Then: I get a set of friendships that contains a Friendship between Nick and Michael
        Assert.assertTrue(friends.stream().filter(f -> f.hasPerson(michael)).findFirst().isPresent());
    }

    @Test
    public void addInterestToPerson() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // When: I add an Interest called Game of Thrones to Nick
        Interest got = new Interest("Game of Thrones");
        nick.addInterest(got);
        // And: I ask if Nick’s is interested in GoT
        Assert.assertTrue(nick.isInterestedIn(got));
    }

    @Test
    public void queryForFamiliarStrangersByInterest() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: a Person named Rob with a phone number 555-234-6777
        Person rob = new Person("Rob", "555-234-6777");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // And: Nick and Michael have a Friendship
        repo.addFriendship(nick, michael);
        repo.verifyFriendship(michael, nick);
        // And: Michael and Rob have a Friendship
        repo.addFriendship(michael, rob);
        repo.verifyFriendship(rob, michael);
        // And: Nick and Rob do not have Friendship
        // And: An Interest Game of Thrones
        Interest got = new Interest("Game of Thrones");
        // And: Nick has an Interest in Game of Thrones
        nick.addInterest(got);
        // And: Rob has an Interest in Game of Thrones
        rob.addInterest(got);
        // When: I query the PersonRepository for FamiliarStrangers by the Interest Game of Thrones
        final Friendship expected = new Friendship(nick, rob);
        Set<Friendship> fof = repo.getFamiliarStrangersByInterest(got);
        // Then: I get back a set of FamiliarStrangers
        // And: the set contains a FamilarStrangers with Nick and Rob through their Friendship with Michael and their shared Interest in Game of Thrones
        Assert.assertTrue(fof.stream().anyMatch(friendship -> friendship.isEquivalentTo(expected)));
    }


    @Test
    public void queryForLotsOfFamiliarStrangersByInterest() {
        // Given: a Person named Nick with a phone number 516-847-4660
        Person nick = new Person("Nick", "516-847-4660");
        // And: a Person named Michael with a phone number 555-111-2233
        Person michael = new Person("Michael", "555-111-2233");
        // And: a Person named Rob with a phone number 555-234-6777
        Person rob = new Person("Rob", "555-234-6777");
        // And: a Person named Ivan with a phone number 555-776-2211
        Person ivan = new Person("Ivan", "555-776-2211");
        // And: a Person named Nathan with a phone number 555-898-1111
        Person nathan = new Person("Nathan", "555-898-1111");
        // And: a Person named Chris with a phone number 555-776-2211
        Person chris = new Person("Chris", "555-776-2211");
        // And: a Person named Joe with a phone number 555-977-2852
        Person joe = new Person("Joe", "555-977-2852");
        // And: the existence of some PersonRepository
        PersonRepository repo = new PersonRepository();
        // And: Nick and Michael have a Friendship
        repo.addFriendship(nick, michael);
        repo.verifyFriendship(michael, nick);
        // And: Michael and Rob have a Friendship
        repo.addFriendship(michael, rob);
        repo.verifyFriendship(rob, michael);
        // And: Ivan and Nick have a Friendship
        repo.addFriendship(ivan, nick);
        repo.verifyFriendship(nick, ivan);
        // And: Ivan and Chris have a Friendship
        repo.addFriendship(ivan, chris);
        repo.verifyFriendship(chris, ivan);
        // And: Nathan and Nick have a Friendship
        repo.addFriendship(nathan, nick);
        repo.verifyFriendship(nick, nathan);
        // And: Joe and Nick have a Friendship
        repo.addFriendship(joe, nick);
        repo.verifyFriendship(nick, joe);
        // And: Nick and Rob do not have Friendship
        // And: An Interest Game of Thrones
        Interest got = new Interest("Game of Thrones");
        // And: Nick has an Interest in Game of Thrones
        nick.addInterest(got);
        // And: Rob has an Interest in Game of Thrones
        rob.addInterest(got);
        // And: Nathan has an Interest in Game of Thrones
        nathan.addInterest(got);
        // And: Ivan has an Interest in Game of Thrones
        ivan.addInterest(got);
        // And: Chris has an Interest in Game of Thrones
        chris.addInterest(got);
        // And: An Interest Programming
        Interest programming = new Interest("Programming");
        // And: Nick has an Interest in Programming
        nick.addInterest(programming);
        // And: Joe has an Interest in Programming
        joe.addInterest(programming);
        // And: Nathan has an Interest in Programming
        nathan.addInterest(programming);
        // When: I query the PersonRepository for FamiliarStrangers by the Interest Game of Thrones
        final Friendship expected = new Friendship(nick, rob);
        Set<Friendship> fof = repo.getFamiliarStrangersByInterest(got);
        // Then: I get back a set of FamiliarStrangers
        // And: the set contains a FamilarStrangers with Nick and Rob through their Friendship with Michael and their shared Interest in Game of Thrones
        Assert.assertTrue(fof.stream().anyMatch(friendship -> friendship.isEquivalentTo(expected)));
    }
}
