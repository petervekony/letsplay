# LetsPlay

Letsplay is the first project in the Java track of grit:lab's curriculum. It consists of creating a basic CRUD API using MongoDB.
Students learn about:
- Spring Boot framework
- Spring Security
- Designing REST APIs
- Authentication
- Authorization
- NoSQL databases
- API security

## Features

The database consists of two collections (users and products) with a one-to-many relationship between them.
Users have five properties: ID, name, email, password and role.
Two user roles are implemented: admin and user. Products have five properties: ID, name, description, price and the owner's ID.
The endpoint to request all products and products by name are accessible without authentication, the rest requires the client to be authenticated by sending the appropriate requests to the auth endpoints.
The API uses HTTPS protocol. Passwords are shared hashed in the database, and not sent back to the clients.

## Environment Variables

Some basic settings (like DB connection or admin password) are stored in the *application.properties* file in the project's *resources* directory.

## Prerequisites

Running the API requires a Java Development Kit installed. This project was written using JDK 17.

Also, the API requires a MongoDB database to connect to. If you want to host the database on another computer or a container, you need to modify the MongoDB host and port variables in the *application.resources* file.

### Hosting The Database Locally

If you want to host the database on localhost, make sure you have mongodb-community installed. On MacOS, you can install it via Homebrew. Run the commands below in your terminal:

```
brew tap mongodb/brew
brew update
brew install mongodb-community
```

You can start and stop the *mongodb-community* service with the following commands:

```
brew services start mongodb-community
brew services stop mongodb-community
```

It can also come in handy if you have a MongoDB GUI (like Compass) installed.

### Running the server

You can run the LetsPlayApplication.java file in the src/main/java/com/petervekony/letsplay directory to start the server.

## Authentication Endpoints

### Sign-up

Using Postman (or a similar API tool) you have to send a POST request to the <font color='green'>*/api/auth/signup*</font> endpoint with the request body following this structure:

```json
{
  "username": "foo",
  "email": "foo@bar.com",
  "password": "desiredPassword"
}
```

### Sign-in

POST request to the <font color='green'>*/api/auth/signin*</font> endpoint with a request body following this structure:

```json
{
  "username": "foo",
  "password": "desiredPassword"
}
```
The response body should look similar to this:
```json
{
  "id": "64edcec97eb59324dc919bbb",
  "username": "foo",
  "email": "foo@bar.com",
  "role": "user"
}
```
It also contains a cookie named "letsplay" for the authentication. Postman saves this cookie by default and uses it in any future requests, with other tools you might have to manually add the cookie.

### Sign-out

Empty POST request to the <font color='green'>*/api/auth/signout*</font> endpoint will log the user out and prompt the client's device to remove the authentication cookie.

## User endpoints

### Get All Users

A GET request with an empty body sent to the <font color='green'>*/api/users*</font> endpoint will retrieve all users.

### Get User By ID

A GET request with an empty body sent to the <font color='green'>*/api/users/**{id}***</font> endpoint will retrieve a user specified at **{id}**.

### Update User
A PUT request to the <font color='green'>*/api/users/**{id}***</font> endpoint specifying to user **{id}** with the following request body structure:
```json
{
  "name": "desiredName",
  "email": "desiredEmail",
  "password": "desiredPassword"
}
```
If either of them is omitted from the request body, the original value remains.

**NOTE**: If you update your own user details, you will have to re-authenticate (<font color='green'>*/api/auth/signin*</font>).

### Update User Role (Admin Only)

Admins can assign a new role to users and admins by adding a *role* field to the **Update User** request:

```json
{
  "role": "admin"
}
```

If other fields are specified as well (name, email or password), all gets updated.
If a user tries to update their role, it is ignored.

### Delete User

A DELETE request with an empty request body to the <font color='green'>*/api/users/**{id}***</font> endpoint with the user **{id}** will delete the user.

Admins can delete any user, users can only delete themselves.

User deletion removes all products owned by the user from the database as well.

## Product endpoints

### Get All Products

An empty GET request to the <font color='green'>*/api/products*</font> endpoint will return all products in the database.

You can also request specific products by name by adding the full name as a path variable like <font color='green'>*/api/products?name=test1*</font>

**NOTE**: This is the only endpoints that is accessible without authentication. User IDs are available in the response, but searching users by ID only works for authenticated clients.

### Get Product By ID

An empty GET request to the <font color='green'>*/api/products/**{id}***</font> endpoint with the **{id}** specified will retrieve the specific product's data.

### Create Product

POST request to the <font color='green'>*/api/products*</font> endpoint with the following structure:

```json
{
  "name": "test1",
  "description": "test1 description",
  "price": 100.00
}
```

A user can NOT create a product for another user.

### Update Product

POST request to the <font color='green'>*/api/products/**{id}***</font> endpoint (where **{id}** is the updated product's ID) with the following request body structure:

```json
{
  "name": "new name",
  "description": "new description",
  "price": 123.12
}
```

### Delete Product

Empty DELETE request to the <font color='green'>*/api/products/**{id}***</font> endpoint with the deleted product's **{id}** will remove it from the database.

**NOTE**: Users can only delete their own products.

