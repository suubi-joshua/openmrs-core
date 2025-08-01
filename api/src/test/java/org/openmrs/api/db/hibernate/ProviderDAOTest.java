/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.ProviderRole;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.ProviderDAO;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderDAOTest extends BaseContextSensitiveTest {
	
	private static final String PROVIDERS_INITIAL_XML = "org/openmrs/api/include/ProviderServiceTest-initial.xml";
	
	@Autowired
	private PersonDAO personDao;
	
	@Autowired
	private ProviderDAO providerDao;
	
	/**
	 * Run this before each unit test in this class.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void runBeforeEachTest() {
		executeDataSet(PROVIDERS_INITIAL_XML);
	}
	
	/**
	 * @see ProviderDAO#getProvidersByPerson(Person,boolean)
	 */
	@Test
	public void getProvidersByPerson_shouldNotReturnRetiredProvidersIfIncludeRetiredFalse() {
		Collection<Provider> providers = providerDao.getProvidersByPerson(personDao.getPerson(2), false);
		assertEquals(1, providers.size());
		assertFalse(providers.iterator().next().getRetired());
	}
	
	/**
	 * @see ProviderDAO#getProvidersByPerson(Person,boolean)
	 */
	@Test
	public void getProvidersByPerson_shouldListRetiredProvidersAtTheEnd() {
		List<Provider> providers = (List<Provider>) providerDao.getProvidersByPerson(personDao.getPerson(2), true);
		assertTrue(providers.get(1).getRetired());
	}
	
	/**
	 * @see ProviderDAO#getProvidersByPerson(Person,boolean)
	 */
	@Test
	public void getProvidersByPerson_shouldReturnAllProvidersIfIncludeRetiredTrue() {
		assertEquals(2, providerDao.getProvidersByPerson(personDao.getPerson(2), true).size());
	}


	/**
	 * @see ProviderDAO#getProviderRole(Integer)
	 */
	@Test
	public void getProviderRole_shouldReturnTheProviderRoleIfExists() {
		assertNotNull(providerDao.getProviderRole(1003));
	}

	/**
	 * @see ProviderDAO#getProviderRole(Integer)
	 */
	@Test
	public void getProviderRole_shouldReturnNullIfNotExists() {
		assertNull(providerDao.getProviderRole(200));
	}

	/**
	 * @see ProviderDAO#getProviderRoleByUuid(String) 
	 */
	@Test
	public void getProviderRoleByUuid_shouldReturnTheProviderRoleIfExists() {
		assertNotNull(providerDao.getProviderRoleByUuid("db7f523f-27ce-4bb2-86d6-6d1d05312bd5"));
	}

	/**
	 * @see ProviderDAO#getProviderRoleByUuid(String)
	 */
	@Test
	public void getProviderRoleByUuid_shouldReturnNullIfNotExists() {
		assertNull(providerDao.getProviderRoleByUuid("wrong-uuid"));
	}

	/**
	 * @see ProviderDAO#getProvidersByRoles(List, boolean) 
	 */
	@Test
	public void getProvidersByRoles_shouldGetActiveProvidersByGivenRoles() {
		Provider providerToRetire = Context.getProviderService().getProvider(1);
		Context.getProviderService().retireProvider(providerToRetire, "test");

		List<ProviderRole> roles = new ArrayList<>();
		roles.add(providerDao.getProviderRole(1001));
		roles.add(providerDao.getProviderRole(1002));

		List<Provider> providers = providerDao.getProvidersByRoles(roles, false);
		assertEquals(4, providers.size());
	}

	/**
	 * @see ProviderDAO#getProvidersByRoles(List, boolean)
	 */
	@Test
	public void getProvidersByRoles_shouldGetAllProvidersIncludingRetiredProvidersByGivenRoles() {
		Provider providerToRetire = Context.getProviderService().getProvider(1);
		Context.getProviderService().retireProvider(providerToRetire, "test");

		List<ProviderRole> roles = new ArrayList<>();
		roles.add(providerDao.getProviderRole(1001));
		roles.add(providerDao.getProviderRole(1002));

		List<Provider> providers = providerDao.getProvidersByRoles(roles, true);
		assertEquals(6, providers.size());
	}
	
}
