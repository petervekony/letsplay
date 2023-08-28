# Letsplay CRUD API

## Signup request

Using Postman (or a similar API tool) you have to send a POST request to the */api/auth/signup* endpoint with the request body following this structure:

```json
{
  "username": "foo",
  "email": "foo@bar.com",
  "password": "desiredPassword"
}
```

## Signin request

POST request to the */api/auth/signin* endpoint with a request body following this structure:

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
It also contains a cookie named "letsplay" for the authentication. Postman saves this cookie by default and uses it in any future requests, with other tools you might have to manually add the cookie.

## User endpoints

### Get All Users

A GET request with an empty body sent to the */api/users* endpoint will retrieve all users.

### Get User By ID

A GET request with an empty body sent to the */api/users/**{id}*** endpoint will retrieve a user specified at **{id}**.

### Create User

??? TODO ???

### Update User
### Delete User

## Product endpoints


## Create product request

POST request to the */api/products* endpoint with the following structure:

```json
{
  "name": "test1",
  "description": "test1 description",
  "price": 100.00
}
```

A user can NOT create a product for another user.

