# MeMatch Spring API Endpoints

## Base URL
All endpoints are prefixed with `/api`

## Users API (`/api/users`)

### GET `/api/users`
- **Description**: Get all users
- **Response**: List of User objects
- **Status**: 200 OK

### GET `/api/users/{id}`
- **Description**: Get user by ID
- **Response**: User object
- **Status**: 200 OK, 404 Not Found

### POST `/api/users`
- **Description**: Create a new user
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "username": "username",
    "passwordHash": "hashed_password"
  }
  ```
- **Response**: Created User object
- **Status**: 201 Created

### PUT `/api/users/{id}/profile`
- **Description**: Update user profile
- **Request Body**:
  ```json
  {
    "description": "User description",
    "profilePictureUrl": "https://example.com/image.jpg"
  }
  ```
- **Response**: Updated User object
- **Status**: 200 OK

### POST `/api/users/{id}/memes`
- **Description**: Post a meme by a user
- **Request Body**:
  ```json
  {
    "imageUrl": "https://example.com/meme.jpg",
    "caption": "Meme caption"
  }
  ```
- **Response**: Created Meme object
- **Status**: 201 Created

### DELETE `/api/users/{userId}/memes/{memeId}`
- **Description**: Delete a user's meme
- **Status**: 204 No Content

## Memes API (`/api/memes`)

### GET `/api/memes`
- **Description**: Get all memes
- **Response**: List of Meme objects
- **Status**: 200 OK

### GET `/api/memes/{memeId}`
- **Description**: Get meme by ID
- **Response**: Meme object
- **Status**: 200 OK, 404 Not Found

### POST `/api/memes/user/{userId}`
- **Description**: Create a new meme
- **Request Body**:
  ```json
  {
    "imageUrl": "https://example.com/meme.jpg",
    "caption": "Meme caption"
  }
  ```
- **Response**: Created Meme object
- **Status**: 201 Created

### GET `/api/memes/user/{userId}`
- **Description**: Get all memes by a specific user
- **Response**: List of Meme objects
- **Status**: 200 OK

### PUT `/api/memes/{memeId}`
- **Description**: Update meme caption
- **Request Body**:
  ```json
  {
    "caption": "Updated caption"
  }
  ```
- **Response**: Updated Meme object
- **Status**: 200 OK

### DELETE `/api/memes/{memeId}`
- **Description**: Delete a meme
- **Status**: 204 No Content

## Matches API (`/api/matches`)

### GET `/api/matches`
- **Description**: Get all matches
- **Response**: List of Match objects
- **Status**: 200 OK

### GET `/api/matches/{matchId}`
- **Description**: Get match by ID
- **Response**: Match object
- **Status**: 200 OK, 404 Not Found

### POST `/api/matches`
- **Description**: Create a new match between two users
- **Request Body**:
  ```json
  {
    "user1Id": 1,
    "user2Id": 2
  }
  ```
- **Response**: Created Match object
- **Status**: 201 Created

### GET `/api/matches/user/{userId}`
- **Description**: Get all matches for a specific user
- **Response**: List of Match objects
- **Status**: 200 OK

### DELETE `/api/matches/{matchId}`
- **Description**: Delete a match
- **Status**: 204 No Content

## Comments API (`/api/comments`)

### GET `/api/comments/{commentId}`
- **Description**: Get comment by ID
- **Response**: Comment object
- **Status**: 200 OK, 404 Not Found

### POST `/api/comments/meme/{memeId}/user/{userId}`
- **Description**: Create a comment on a meme
- **Request Body**:
  ```json
  {
    "content": "Comment text"
  }
  ```
- **Response**: Created Comment object
- **Status**: 201 Created

### GET `/api/comments/meme/{memeId}`
- **Description**: Get all comments for a specific meme
- **Response**: List of Comment objects
- **Status**: 200 OK

### PUT `/api/comments/{commentId}`
- **Description**: Update a comment
- **Request Body**:
  ```json
  {
    "content": "Updated comment text"
  }
  ```
- **Response**: Updated Comment object
- **Status**: 200 OK

### DELETE `/api/comments/{commentId}`
- **Description**: Delete a comment
- **Status**: 204 No Content

## Likes API (`/api/likes`)

### POST `/api/likes/meme/{memeId}/user/{userId}`
- **Description**: Like a meme
- **Response**: Created Like object
- **Status**: 201 Created, 400 Bad Request (if already liked)

### DELETE `/api/likes/meme/{memeId}/user/{userId}`
- **Description**: Unlike a meme
- **Status**: 204 No Content, 404 Not Found

### GET `/api/likes/meme/{memeId}/user/{userId}`
- **Description**: Check if a user has liked a meme
- **Response**:
  ```json
  {
    "hasLiked": true
  }
  ```
- **Status**: 200 OK

### GET `/api/likes/meme/{memeId}/count`
- **Description**: Get the total number of likes for a meme
- **Response**:
  ```json
  {
    "count": 42
  }
  ```
- **Status**: 200 OK

## Error Handling

All endpoints use a global exception handler that returns standardized error responses:

```json
{
  "status": 404,
  "message": "Resource not found",
  "timestamp": "2024-01-01T12:00:00"
}
```

### HTTP Status Codes
- **200 OK**: Successful GET, PUT requests
- **201 Created**: Successful POST requests
- **204 No Content**: Successful DELETE requests
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected server errors

