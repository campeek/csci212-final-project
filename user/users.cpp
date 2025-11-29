// users.cpp
// Cameron Peek

#include <string>
#include <vector>
#include <sstream>
#include <fstream>

using namespace std;

struct User {
    string name;
    string password;
    string role;
};


User loadUserFromLine(string& line){
    stringstream ss(line);

    string name, password, role;

    getline(ss, name, ',');
    getline(ss, password, ',');
    getline(ss, role, ',');
    return {name,password,role};
}

vector<User> load_users(string path) {
    vector<User> users;
    ifstream file(path);

    string line;
    while (getline(file, line)) {
        if (!line.empty()) {
            users.push_back(loadUserFromLine(line));
        }
    }
    return users;
}