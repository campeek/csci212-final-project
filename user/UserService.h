// UserService.h
// Cameron Peek

#ifndef CSCI212_FINAL_PROJECT_USERSERVICE_H
#define CSCI212_FINAL_PROJECT_USERSERVICE_H

#include "users.cpp"
#include <optional>

class UserService {
public:
    explicit UserService(const string& usersfile);


    const vector<User>& getUsers() const;
    optional<User> getUser(const string& name) const;

private:
    vector<User> users;
    string usersfile;
};

#endif //CSCI212_FINAL_PROJECT_USERSERVICE_H