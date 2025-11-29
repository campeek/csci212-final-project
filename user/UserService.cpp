// UserService.cpp
// Cameron Peek

#include "UserService.h"
#include "users.h"
#include <algorithm>

UserService::UserService (const std::string& file)
    : usersfile(file),
    users(load_users(file)){}

const std::vector<User>& UserService::getUsers() const {
    return users;
}

std::optional<User> UserService::getUser(const std::string &name) const {
    auto it = std::find_if(users.begin(), users.end(), [&](const User &user) {
        return user.name == name;
    });

    if (it == users.end()) {
        return std::nullopt;
    }
    return *it;
}


