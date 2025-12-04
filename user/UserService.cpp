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

std::optional<User> UserService::getUserByName(const std::string &name) const {
    auto it = std::find_if(users.begin(), users.end(), [&](const User &user) {
        return user.name == name;
    });

    if (it == users.end()) {
        return std::nullopt;
    }
    return *it;
}

User UserService::addUser(const std::string &name, const std::string &password) {
    int id = users.size();
    User newUser = {id, name, password};
    serialize(users, usersfile);

    return newUser;
}

void UserService::removeUser(int id) {
    for (auto it = users.begin(); it != users.end(); it++) {
        if (it->id == id) {
            users.erase(it);
        }
    }
}


