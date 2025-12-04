// users.cpp
// Cameron Peek

#include "users.h"

#include <string>
#include <vector>
#include <sstream>
#include <fstream>

User loadUserFromLine(std::string& line){
    std::stringstream ss(line);

    std::string id, name, password, role;

    getline(ss,id,',');
    getline(ss, name, ',');
    getline(ss, password, ',');
    getline(ss, role, ',');

    //TODO: handle malformed data. id not parsing to int breaks stuff
    return {std::stoi(id), name,password,role};
}

std::vector<User> load_users(const std::string& path) {
    std::vector<User> users;

    // TODO: exception handling
    std::ifstream file(path);

    std::string line;
    while (getline(file, line)) {
        if (!line.empty()) {
            users.push_back(loadUserFromLine(line));
        }
    }
    return users;
}

static void serialize(std::vector<User>& users, std::string path) {
    // TODO: exception handling
    std::ofstream file(path);
    for (User& user : users) {
        file << user.name << "," << user.password << "," << user.role << std::endl;
    }
    file.close();
}