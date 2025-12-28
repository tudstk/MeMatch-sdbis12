# Authentication Guide for MeMatch

## Overview

MeMatch uses **JWT (JSON Web Tokens)** for authentication. Users must register and login to receive a JWT token, which is then used to access protected endpoints.

## Authentication Flow

1. **Register** → User creates an account → Receives JWT token
2. **Login** → User authenticates → Receives JWT token
3. **Access Protected Endpoints** → Include JWT token in Authorization header

## API Endpoints

### 1. Register (Public)

**POST** `/api/auth/register`

Create a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "username": "username",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "username",
  "email": "user@example.com"
}
```

**Validation Rules:**
- Email: Must be a valid email format
- Username: 3-50 characters
- Password: Minimum 6 characters

**Error Responses:**
- `400 Bad Request`: Invalid input data or email/username already exists
- `400 Bad Request`: Email already exists
- `400 Bad Request`: Username already exists

### 2. Login (Public)

**POST** `/api/auth/login`

Authenticate with username/email and password.

**Request Body:**
```json
{
  "usernameOrEmail": "username",
  "password": "password123"
}
```

**Note:** You can use either username or email in the `usernameOrEmail` field.

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "username",
  "email": "user@example.com"
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid credentials

## Using JWT Tokens

### How to Include Token in Requests

After registering or logging in, you'll receive a JWT token. Include this token in the **Authorization** header of all protected requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Example with cURL

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'

# Access protected endpoint (use token from login/register response)
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### Example with JavaScript (Fetch API)

```javascript
// Register
const registerResponse = await fetch('http://localhost:8080/api/auth/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'test@example.com',
    username: 'testuser',
    password: 'password123'
  })
});

const registerData = await registerResponse.json();
const token = registerData.token;

// Use token for protected endpoints
const usersResponse = await fetch('http://localhost:8080/api/users', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const users = await usersResponse.json();
```

## Protected vs Public Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /swagger-ui/**` - Swagger documentation
- `GET /v3/api-docs/**` - OpenAPI specification

### Protected Endpoints (Authentication Required)
All other endpoints require a valid JWT token:
- `/api/users/**` - User management
- `/api/memes/**` - Meme operations
- `/api/matches/**` - Match operations
- `/api/comments/**` - Comment operations
- `/api/likes/**` - Like operations

## Using Swagger UI with Authentication

1. **Start your application**: `mvn spring-boot:run`
2. **Open Swagger UI**: `http://localhost:8080/swagger-ui.html`
3. **Register or Login**:
   - Use `POST /api/auth/register` or `POST /api/auth/login`
   - Copy the token from the response
4. **Authorize in Swagger**:
   - Click the **"Authorize"** button at the top right
   - Enter: `Bearer YOUR_TOKEN_HERE` (or just the token, Swagger will add "Bearer")
   - Click **"Authorize"**
   - Click **"Close"**
5. **Test Protected Endpoints**:
   - Now you can test any protected endpoint
   - Swagger will automatically include the token in requests

## Token Details

- **Token Type**: JWT (JSON Web Token)
- **Algorithm**: HS256 (HMAC SHA-256)
- **Expiration**: 24 hours (86400000 milliseconds)
- **Contains**: User ID and username

## Security Features

1. **Password Encryption**: Passwords are hashed using BCrypt before storage
2. **JWT Tokens**: Stateless authentication using secure tokens
3. **Token Validation**: All tokens are validated on each request
4. **CORS Enabled**: Cross-origin requests are allowed (configured in SecurityConfig)

## Configuration

### JWT Configuration (application.properties)

```properties
# JWT Secret Key (change this in production!)
jwt.secret=MeMatchSecretKeyForJWTTokenGenerationAndValidationMustBeAtLeast256BitsLongForSecurity

# Token expiration in milliseconds (24 hours = 86400000)
jwt.expiration=86400000
```

**⚠️ Important**: Change the `jwt.secret` in production to a secure, randomly generated key!

## Error Handling

### Common Authentication Errors

1. **401 Unauthorized**
   - Invalid or missing token
   - Expired token
   - Invalid credentials on login

2. **403 Forbidden**
   - Valid token but insufficient permissions (if role-based access is added)

3. **400 Bad Request**
   - Invalid request body format
   - Validation errors (email format, password length, etc.)
   - Email/username already exists (on registration)

## Example Workflow

### Complete Authentication Flow

```bash
# Step 1: Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "username": "johndoe",
    "password": "securepass123"
  }'

# Response contains token - save it!
# Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# Step 2: Use token to access protected endpoints
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Step 3: Create a meme (requires authentication)
curl -X POST http://localhost:8080/api/memes/user/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "imageUrl": "https://example.com/meme.jpg",
    "caption": "Funny meme!"
  }'
```

## Testing Authentication

### Using Postman

1. Create a new request for `/api/auth/register`
2. Set method to POST
3. Add JSON body with email, username, password
4. Send request and copy the token
5. For protected endpoints:
   - Go to "Authorization" tab
   - Select "Bearer Token"
   - Paste your token
   - Send request

### Using Swagger UI

1. Register/Login through Swagger
2. Click "Authorize" button
3. Enter token
4. Test protected endpoints

## Troubleshooting

### Token Not Working
- Check if token is expired (tokens expire after 24 hours)
- Ensure token is included as: `Bearer {token}`
- Verify token wasn't modified or corrupted

### Can't Register
- Check if email/username already exists
- Verify password meets requirements (min 6 characters)
- Check email format is valid

### Can't Login
- Verify username/email is correct
- Check password is correct
- Ensure user exists in database

### 401 Unauthorized on Protected Endpoints
- Token might be expired - login again to get new token
- Token format incorrect - must be `Bearer {token}`
- Token not included in request

## Security Best Practices

1. **Change JWT Secret**: Use a strong, randomly generated secret in production
2. **HTTPS**: Always use HTTPS in production to protect tokens in transit
3. **Token Storage**: Store tokens securely (not in localStorage for sensitive apps)
4. **Token Expiration**: Tokens expire after 24 hours - implement refresh tokens for production
5. **Password Strength**: Enforce strong password policies
6. **Rate Limiting**: Implement rate limiting on login/register endpoints

## Next Steps

For production, consider adding:
- Refresh tokens for longer sessions
- Password reset functionality
- Email verification
- Two-factor authentication (2FA)
- Role-based access control (RBAC)
- Rate limiting
- Account lockout after failed attempts

