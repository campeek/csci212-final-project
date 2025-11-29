// users.h
// Cameron Peek

#ifndef CSCI212_FINAL_PROJECT_USERS_H
#define CSCI212_FINAL_PROJECT_USERS_H
#include <string>
#include <vector>

struct User {
    std::string name;
    std::string password;
    std::string role;
};

std::vector<User> load_users(const std::string& path);
void serialize_users(const std::vector<User>& users, const std::string& path);

#endif //CSCI212_FINAL_PROJECT_USERS_H