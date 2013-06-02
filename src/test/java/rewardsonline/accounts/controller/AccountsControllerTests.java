package rewardsonline.accounts.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.ui.ExtendedModelMap;

import rewardsonline.accounts.Account;
import rewardsonline.accounts.Customer;
import rewardsonline.accounts.StubAccountManager;
import rewardsonline.accounts.web.AccountsController;

import com.sun.net.httpserver.HttpPrincipal;

/**
 * Unit test for the AccountController implementation.
 */
public class AccountsControllerTests {

	private AccountsController controller;

	private StubAccountManager accountManager;

	@Before
	public void setUp() throws Exception {
		accountManager = new StubAccountManager();
		controller = new AccountsController(accountManager);
	}

	@Test
	public void testList() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String view = controller.list(new HttpPrincipal(
				StubAccountManager.TEST_USER, StubAccountManager.TEST_USER),
				model);
		assertEquals("accounts/list", view);
		Customer customer = (Customer) model.get("customer");

		assertNotNull(customer);
		assertEquals(1, customer.getAccounts().size());
	}

	@Test
	@DirtiesContext
	public void testShow() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String view = controller.show(StubAccountManager.TEST_ACCOUNT_NUMBER,
				model);
		assertEquals("accounts/show", view);
		assertEquals(StubAccountManager.TEST_ACCOUNT_NUMBER,
				((Account) model.get("account")).getNumber());
	}

}