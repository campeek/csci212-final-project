### CSCI212 Final Project

Git repo for our project

***

### Docs (in no particular order)
idk this can probably look prettier 

**User**
> User is a struct to represent, well, users.  
> It has the following fields
> - id - numerical user ID
> - name - username
> - password - password (not hashed or anything **DO NOT PUT YOUR ACTUAL PASSWORD IN HERE**)
> - roles - what permissions the user has

**UserService**
> UserService acts as an interface between serialized (saved) user data and our project.  
> It's responsible for CRUD operations on the User database
> - creating Users
> - retrieving Users
> - updating Users
> - deleting Users

It has the following functions
| Name | Return Type | Parameters |
|------|-------------|------------|
| getUsers | vector <User> | none |
| addUser | void | string name, string password |
| removeUser | void | int id |
| getUserByName | optional <User> | string name |
