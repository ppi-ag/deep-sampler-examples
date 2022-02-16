/*
 *
 *  * Copyright 2020 PPI AG (Hamburg, Germany)
 *  * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.examples.helloworld;


import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * An example for a Service that would give access to person related data.
 * This Service is used to create an indirection between {@link GreetingService} and {@link PersonDaoImpl} to simulate
 * a bigger compound and to demonstrate how DeepSampler is able to stub iobjects that are not directly accessible
 * by the object that is under test.
 */
public class PersonService {

    @Inject
    private PersonDao personDao;

    /**
     * Loads a person using the personId and retrieves the name of the person.
     * @param personId The id of the person
     * @return The name of the person
     */
    public String getName(int personId) {
        return personDao.loadPerson(personId).getName();
    }

    /**
     * Loads a person using the personId and retrieves the name of the person.
     * @param personId The id of the person
     * @return The name of the person
     */
    public String getName(PersonId personId) {
        return personDao.loadPerson(personId).getName();
    }

    /**
     * Loads a person using the personId and retrieves the birthday of the person.
     * @param personId The id of the person
     * @return The birthday of the person
     */
    public LocalDateTime getBirthday(int personId) {
        return personDao.loadPerson(personId).getBirthday();
    }
}
