/*
 *
 *  * Copyright 2020 PPI AG (Hamburg, Germany)
 *  * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.examples.helloworld;

import javax.inject.Singleton;
import java.time.LocalDateTime;

/**
 * Simulates a DataAccessObject that would load a {@link Person}. In real life the {@link Person} would be loaded from
 * a DataBase. This would be a typical object that needs to be stubbed in order to gain control over test data for tests.
 * So this DAO will be stubbed when {@link GreetingService} is being tested.
 */
@Singleton
public class PersonDaoImpl implements PersonDao {

    private String name = "Geordi La Forge";
    private static final LocalDateTime GEORDIS_BIRTHDAY = LocalDateTime.of(2335, 2, 16, 0, 0);

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Simulates loading {@link Person}s from a DataBase. This method will be stubbed by DeepSampler. The concrete stubbing is
     * done by the test case.
     *
     * @param personId The id of the {@link Person} that will be loaded.
     * @return The loaded {@link Person}. This object will be replaced by a Sample if loadPerson() is stubbed by DeepSampler.
     */
    public Person loadPerson(int personId) {
        return new Person(name, GEORDIS_BIRTHDAY);
    }

    /**
     * Simulates loading {@link Person}s from a DataBase. This method will be stubbed by DeepSampler. The concrete stubbing is
     * done by the test case.
     *
     * @param personId The id of the {@link Person} that will be loaded.
     * @return The loaded {@link Person}. This object will be replaced by a Sample if loadPerson() is stubbed by DeepSampler.
     */
    public Person loadPerson(PersonId personId) {
        return new Person(name, GEORDIS_BIRTHDAY);
    }
}
