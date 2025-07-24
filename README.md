# Booking
[![wakatime](https://wakatime.com/badge/user/04527014-5266-4470-880c-17f5f4b81957/project/03bd4bd9-ccab-42cc-a326-fd7f019b4401.svg)](https://wakatime.com/badge/user/04527014-5266-4470-880c-17f5f4b81957/project/03bd4bd9-ccab-42cc-a326-fd7f019b4401)

A backend implementation of a coworking reservation service.

(was out of time for frontend unfortunately)

## Bloopers
Here are things about my service that shouldn't be done that way, or they weren't done at all.
Most of them were done not because of lacking experience, but because of shortness on time.

Here is the list of crutches and unfinished things I am aware of (and how I would have fixed them, had I more time).

### Common
#### Testing
I have a folder in Postman to test things, but I know unit tests are life. There was no time for them
unfortunately, plus my structure planning for the project changed rapidly and lots of times (like using a session
storage vs JWT; verify JWT on the auth side instead of the backend microservice).

### Backend microservice (spring)
#### Database migration
There is virtually none. I am somewhat used to Alembic that is orthodox and automatically generates DDL & DML to handle 
the database, but I had no time to learn these tools for Java.

### Auth microservice (go)

#### Framework picking
...there isn't one for this service. I used most primitive and popular Go's tooling for building the API, those being 
GORM and internal HTTP library (which, to my surprise, was aware of such things as routes and serving them).
As I am not proficient in Go (at all), I considered the internal tools to be the best in terms of documentation,
learning and usage.

#### Responses
I made a miserable wrapper that is sometimes used to send JSON like `{"error": "Foo"}`, but lots of times I 
confined to just send an error code. Considering the scale and complexity of the microservice this is actually enough, 
but I am sentient of it being a bad practice.

#### Middleware
I didn't write the middleware to inject token claims into the context like I did with the database.
Considering we only need three routes for JWT checking it's decent, but it still bugs me.

#### JWT
I could've issued JWT signed with RSA and then expose the public key to verify them for the Spring backend.
This way I would avoid overhead sending each JWT to verify on the Go's side, but I decided not to change that
mid-project (plus, the spec states that Go must be responsible for both authentication and authorization).



### Deploying
#### Default user creation
Creating a default admin user makes sense, but its creation should be handled by the services. As I had complications 
interconnecting the auth and backend microservices on startup, I had to embed the default user creation in the 
healthcheck. Theoretically it is an ideal place for it: the table initialization scripts run before it, and it prevents 
other services from running before itself, but it runs twice a minute for no reason.
