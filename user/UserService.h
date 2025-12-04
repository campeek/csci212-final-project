// UserService.h
// Cameron Peek

#ifndef CSCI212_FINAL_PROJECT_USERSERVICE_H
#define CSCI212_FINAL_PROJECT_USERSERVICE_H

#include "users.cpp"
#include <optional>

class UserService {
public:
    explicit UserService(const std::string& file);

    const std::vector<User>& getUsers() const;
    std::optional<User> getUserByName(const std::string& name) const;
    User addUser(const std::string& name, const std::string& password);
    void removeUser(int id);

private:
    std::string usersfile;
    std::vector<User> users;
};

#endif //CSCI212_FINAL_PROJECT_USERSERVICE_H