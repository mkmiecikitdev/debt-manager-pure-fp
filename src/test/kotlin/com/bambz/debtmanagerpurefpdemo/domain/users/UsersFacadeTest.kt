package com.bambz.debtmanagerpurefpdemo.domain.users

import com.bambz.debtmanagerpurefpdemo.TestUtils.assertMonoEitherLeft
import com.bambz.debtmanagerpurefpdemo.TestUtils.assertMonoEitherRight
import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsModule
import com.bambz.debtmanagerpurefpdemo.domain.errors.UnauthorizedError
import com.bambz.debtmanagerpurefpdemo.domain.errors.UserExistError
import com.bambz.debtmanagerpurefpdemo.domain.users.api.LoginUserDto
import com.bambz.debtmanagerpurefpdemo.domain.users.api.NewUserDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersFacadeTest {

    private lateinit var facade: UsersFacade

    @BeforeEach
    fun setup() {
        val debtsInMemoryFacade = DebtsModule().createInMemoryFacade()
        facade = UsersModule().createInMemoryFacade(debtsInMemoryFacade)
    }

    @Test
    fun shouldAddCustomer() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")

        // when
        val result = facade.addUser(form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.email)
            assertTrue { it.roles.contains(Role.USER) }
            assertTrue { !it.roles.contains(Role.ADMIN) }
        }
    }

    @Test
    fun shouldAddOwner() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")

        // when
        val result = facade.addAdmin(form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.email)
            assertTrue { it.roles.contains(Role.USER) }
            assertTrue { it.roles.contains(Role.ADMIN) }
        }
    }

    @Test
    fun shouldReturnUserExistsError() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")
        facade.addUser(form).block()

        // when
        val result = facade.addAdmin(form)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(UserExistError("user1"), it)
        }
    }

    @Test
    fun shouldSuccessLogin() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")
        facade.addAdmin(form).block()

        // when
        val result = facade.login(LoginUserDto(email = "user1", password = "pass"))

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.email)
            assertEquals(2, it.roles.size())
            assertTrue { it.roles.contains(Role.USER) }
            assertTrue { it.roles.contains(Role.ADMIN) }
        }
    }

    @Test
    fun shouldReturnUnauthorizedIfLoginInvalid() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")
        val invalidLoginForm = LoginUserDto(email = "XXX", password = "pass")
        facade.addAdmin(form).block()

        // when
        val result = facade.login(invalidLoginForm)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(UnauthorizedError, it)
        }
    }

    @Test
    fun shouldReturnUnauthorizedIfPasswordInvalid() {
        // given
        val form = NewUserDto(email = "user1", password = "pass", confirmPassword = "pass")
        val invalidLoginForm = LoginUserDto(email = "user1", password = "XXX")
        facade.addAdmin(form).block()

        // when
        val result = facade.login(invalidLoginForm)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(UnauthorizedError, it)
        }
    }
}