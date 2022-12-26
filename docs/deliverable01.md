# Build status

[![CircleCI](https://circleci.com/gh/professor-forward/project-team-pineapple/tree/f%2Fdeliverable02.svg?style=svg&circle-token=a721d42ee48ce18a4640660f0845dab2adfaed56)](https://circleci.com/gh/professor-forward/project-team-pineapple/tree/f%2Fdeliverable02)

# Team Pineapple

## Team Members

| Name | Student Number |
| - | - |
| Aksh Babbar | 300034042 |
| Cameron Cardiff |300068033|
| Ben Herweyer | 300097196 |
| Daniel Tang | 0300068985 |
| Rohaan Williams | 300010136 |

# Domain Model

![](assets/classDiagram.svg)

# About deliverable 1

Implemented user login/out and registration along with minimal dashboards welcoming each user type.

Note:
- Logout button is in the options menu.
- The Login class is part of the system model, not the domain model. It caches the session of the currently logged-in user.
- Project uses SQLite via Room and RxJava as its database. See user.UserDao and user.UserDb
- Project uses AndroidX support library to support the above and a nice UI.
- Project uses Repository abstractions to allow easily adding Firebase or other API support in the future.
