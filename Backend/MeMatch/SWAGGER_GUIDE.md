# Swagger/OpenAPI Guide for MeMatch

## What is Swagger?

Swagger (now called OpenAPI) is a tool that automatically generates interactive API documentation for your Spring Boot application. It provides a web-based UI where you can:
- View all your API endpoints
- See request/response schemas
- Test API endpoints directly from the browser
- Understand what parameters are required
- See example requests and responses

## How to Start Your Application

### Prerequisites
1. **Java 17** installed
2. **Maven** installed
3. **PostgreSQL** running on `localhost:5433` with database `mematch`
4. Database credentials (username: `postgres`, password: `1234`)

### Starting the Application

#### Option 1: Using Maven (Recommended)
```bash
# Navigate to your project directory
cd E:\.master\Multi-tiered-SD\proiect\Backend\MeMatch

# Start the Spring Boot application
mvn spring-boot:run
```

#### Option 2: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Locate `MeMatchApplication.java` in `src/main/java/org/example/mematch/`
3. Right-click and select "Run MeMatchApplication"
4. Or use the Run button in your IDE

#### Option 3: Build and Run JAR
```bash
# Build the project
mvn clean package

# Run the JAR file
java -jar target/MeMatch-1.0-SNAPSHOT.jar
```

### Verify Application is Running
Once started, you should see output like:
```
Started MeMatchApplication in X.XXX seconds
```

The application runs on **http://localhost:8080** by default.

## Accessing Swagger UI

Once your application is running, you can access Swagger in two ways:

### 1. Swagger UI (Interactive Documentation)
Open your web browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```
or
```
http://localhost:8080/swagger-ui/index.html
```

### 2. OpenAPI JSON Specification
View the raw OpenAPI specification at:
```
http://localhost:8080/v3/api-docs
```

## How to Use Swagger UI

### 1. **Explore API Endpoints**
- Swagger UI displays all your API endpoints organized by tags (Users, Memes, Matches, Comments, Likes)
- Each endpoint shows:
  - HTTP method (GET, POST, PUT, DELETE)
  - Endpoint path
  - Description
  - Parameters required
  - Request body schema
  - Response codes and schemas

### 2. **Test an Endpoint**

#### Example: Creating a User
1. Find the **Users** section
2. Click on `POST /api/users` to expand it
3. Click the **"Try it out"** button
4. Fill in the request body:
   ```json
   {
     "email": "john.doe@example.com",
     "username": "johndoe",
     "passwordHash": "hashed_password_123"
   }
   ```
5. Click **"Execute"**
6. View the response below:
   - **Response Code**: 201 (Created)
   - **Response Body**: The created user object
   - **Response Headers**: Location and other headers

#### Example: Getting All Users
1. Find `GET /api/users`
2. Click **"Try it out"**
3. Click **"Execute"**
4. See the list of all users in the response

#### Example: Creating a Meme
1. First, create a user (as shown above) and note the user ID from the response
2. Go to **Memes** section
3. Click `POST /api/memes/user/{userId}`
4. Click **"Try it out"**
5. Enter the `userId` in the path parameter field
6. Fill in the request body:
   ```json
   {
     "imageUrl": "https://example.com/meme.jpg",
     "caption": "This is a funny meme!"
   }
   ```
7. Click **"Execute"**

### 3. **Understanding the Interface**

#### Endpoint Details
- **Parameters**: Shows path variables, query parameters, and headers
- **Request Body**: Schema showing required fields and data types
- **Responses**: Different HTTP status codes and their meanings
  - `200 OK`: Success
  - `201 Created`: Resource created
  - `204 No Content`: Success with no response body
  - `400 Bad Request`: Invalid input
  - `404 Not Found`: Resource not found
  - `500 Internal Server Error`: Server error

#### Schema Models
- Click on any model name (like `User`, `Meme`, `Comment`) to see its structure
- Shows all fields, their types, and whether they're required

### 4. **Common Operations**

#### Testing GET Requests
- Usually no request body needed
- May require path parameters (like `/api/users/{id}`)
- Click "Try it out", enter parameters, then "Execute"

#### Testing POST/PUT Requests
- Require a request body
- Fill in the JSON body according to the schema
- Required fields are marked with a red asterisk (*)

#### Testing DELETE Requests
- Usually only need path parameters
- Returns 204 No Content on success

## Customizing Swagger Configuration

The Swagger configuration is in `OpenApiConfig.java`. You can customize:
- API title and description
- Contact information
- License information
- Server URLs
- API version

## Troubleshooting

### Swagger UI Not Loading
1. **Check if application is running**: Visit `http://localhost:8080` - you should get a response
2. **Check the port**: Make sure port 8080 is not in use by another application
3. **Check Maven dependencies**: Run `mvn clean install` to ensure dependencies are downloaded
4. **Check logs**: Look for errors in the console output

### Endpoints Not Showing
1. **Check controller annotations**: Make sure `@RestController` and `@RequestMapping` are present
2. **Check package scanning**: Ensure controllers are in a package scanned by Spring Boot
3. **Restart the application**: Sometimes a restart is needed after adding new endpoints

### Database Connection Issues
- Ensure PostgreSQL is running
- Check database credentials in `application.properties`
- Verify database `mematch` exists

## Tips for Using Swagger

1. **Use Swagger for Development**: It's perfect for testing your API during development
2. **Share with Team**: The Swagger UI URL can be shared with frontend developers
3. **Documentation**: Swagger serves as live documentation that stays in sync with your code
4. **Testing**: Use it to quickly test endpoints without writing separate test clients
5. **Schema Validation**: Swagger validates your request bodies before sending

## Example Workflow

Here's a complete example workflow:

1. **Start the application**: `mvn spring-boot:run`
2. **Open Swagger UI**: `http://localhost:8080/swagger-ui.html`
3. **Create a user**:
   - POST `/api/users` with user data
   - Copy the user ID from response
4. **Create a meme**:
   - POST `/api/memes/user/{userId}` with meme data
   - Copy the meme ID from response
5. **Like the meme**:
   - POST `/api/likes/meme/{memeId}/user/{userId}`
6. **Add a comment**:
   - POST `/api/comments/meme/{memeId}/user/{userId}` with comment content
7. **View all memes**:
   - GET `/api/memes`

## Additional Resources

- **SpringDoc OpenAPI Documentation**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/
- **Swagger UI**: https://swagger.io/tools/swagger-ui/

## Summary

Swagger provides an interactive way to:
- ✅ View all your API endpoints
- ✅ Understand request/response formats
- ✅ Test endpoints without writing code
- ✅ Share API documentation with your team
- ✅ Validate your API design

Just start your application and visit `http://localhost:8080/swagger-ui.html` to get started!

