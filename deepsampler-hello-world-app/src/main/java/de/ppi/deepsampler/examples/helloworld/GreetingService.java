/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.helloworld;


import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * This is an example for a compound that we want to test. The GreetingService creates greeting messages
 * for a particular person. The person is loaded by DataAccessObject {@link PersonDaoImpl}, but {@link PersonService}
 * does not use the {@link PersonDaoImpl} directly, instead it uses a {@link PersonService} that in turn calls the
 * {@link PersonDaoImpl}. This additional indirection is intended to simulate a bigger compound and to show, how DeepSampler
 * is able to stub an object that is not directly accessible by the object that is being tested, or by the
 * test case itself.
 */
public class GreetingService {

    @Inject
    private PersonService personService;

    /**
     * Creates a greeting message for a person identified by an id.
     * @param personId The id of the person that we want to greet
     * @return a greeting for personId
     */
    public String createGreeting(int personId) {
        String name = personService.getName(personId);

        return String.format("Hello %s!", name);
    }

    public String createBirthdayGreeting(int personId) {
        String name = personService.getName(personId);
        LocalDateTime birthday = personService.getBirthday(personId);

        return String.format("%s's Birthday: %2$te.%2$tm.%2$tY",name, birthday);
    }

    /**
     * Creates a greeting message for a person identified by a {@link PersonId}, which comes without an equals().
     * We use this method to demonstrate, how custom matchers can be used to replace a missing equals() method in
     * parameter values.
     *
     * @param personId The id of the person that we want to greet
     * @return a greeting for personId
     */
    public String createGreeting(PersonId personId) {
        String name = personService.getName(personId);

        return String.format("Hello %s!", name);
    }
}
