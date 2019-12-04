package com.bambz.debtmanagerpurefpdemo.domain.users

class UsersModule {

    fun createInMemoryFacade(): UsersFacade {
        return UsersFacade(InMemoryUsersRepository())
    }

}