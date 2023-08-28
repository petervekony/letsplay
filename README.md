# Letsplay CRUD API

## Signup request

Using Postman (or a similar API tool) you have to send a POST request to the */api/auth/signup* endpoint with the request body following this structure:

```json
{
  "username": "foo",
  "email": "foo@bar.com",
  "roles": [
    "list",
    "of",
    "roles"
  ],
  "password": "desiredPassword"
}
```

Currently "admin" and "user" roles are implemented. Users can only update and delete their own user profile, and only add, delete and update products belonging to their own user id.

## Signing request

```json
{
  "username": "foo",
  "password": "desiredPassword"
}
```
The response body should look similar to this:
```json
{
    "id": "64ecf805e4293c695a01c2c3",
    "username": "foo",
    "email": "foo@bar.com",
    "roles": [
        "user"
    ]
}
```
It also contains a cookie named "letsplay" for the authentication.

## Create product request

