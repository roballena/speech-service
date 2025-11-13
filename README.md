# Speech API

## Application Requirements

A politician will give many speeches in their political career. Many politicians currently draft their speeches in an
ad-hoc fashion using Microsoft Word and share versions with their team using email. This process is inefficient as it
does not allow for the users to centrally store, archive, and search their speeches. This challenge will require you to
design a portion of a system to automate this process.

For the tech challenge, this system will have the capability for a politician to do the following:

1. View all their speeches saved in the system.
2. Add a speech to the system (for this challenge we will consider a speech to have the actual text, the author
   information, keywords about the speech and a speech date).
3. Edit a speech or its metadata
4. Delete a speech
5. The ability to search the speeches – (search by author, date range, subject area, or snippets of text from the speech
   body.)

---

## Features

### **GET /api/speeches**

Returns all the speeches stored in the system.

**Request**

```bash
curl --location 'localhost:8080/api/speeches'
```

**Responses**

| Status | Description              |
|--------|--------------------------|
| 200    | Returns list of speeches |

---

### **GET /api/speeches/{id}**

Returns a specific speech by ID.

**Request**

```bash
curl --location 'localhost:8080/api/speeches/1'
```

**Responses**

| Status | Description             |
|--------|-------------------------|
| 200    | Returns specific speech |
| 404    | Speech not found        |

---

### **POST /api/speeches/**

Creates a new speech.

**Request**

```bash
curl --location 'localhost:8080/api/speeches' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "This is a sample speech5",
    "author": "Robby",
    "authorEmail": "robby@email.com",
    "keywords": ["sample", "speech5", "robby"],
    "speechDate": "2025-11-15"
}'
```

---

### **PUT /api/speeches/{id}**

Updates an existing speech.

**Request**

```bash
curl --location --request PUT 'localhost:8080/api/speeches/2' \
--header 'Content-Type: application/json' \
--data '{
    "text": "This is a second sample speech. (edited)"
}'
```

**Responses**

| Status | Description                 |
|--------|-----------------------------|
| 200    | Speech updated successfully |
| 404    | Speech not found            |

---

### **DELETE /api/speeches/{id}**

Updates an existing speech.

**Request**

```bash
curl --location --request DELETE 'localhost:8080/api/speeches/3'
```

**Responses**

| Status | Description                 |
|--------|-----------------------------|
| 204    | Speech deleted successfully |
| 404    | Speech not found            |

---

### **DELETE /api/speeches/search**

Searches speeches based on provided criteria.
Criteria can include author, date range, keywords, or text snippets.

**Request**

```bash
curl --location 'localhost:8080/api/speeches/search?keyword=speech'
```

**Responses**

| Status | Description                                       |
|--------|---------------------------------------------------|
| 200    | Returns list of speeches based on search criteria |
| 400    | Invalid search parameters                         |

## How to Run

Run the following command to start the application using Docker Compose:

```
docker-compose up --build
```

## Tech Stack

- Java 17+
- Spring Boot 3+
- Spring Web
- JUnit 5
- Mockito

## Improvements

1. **Authentication & Authorization**: Implement user authentication to ensure that only authorized users can access and
   modify speeches.
2. **Validation**: Add more robust validation for input data using Spring's validation framework.
3. **Pagination**: Add pagination to the GET /api/speeches endpoint to handle large datasets efficiently.
4. **Versioning**: Implement version control for speeches to track changes over time.
5. **MapStruct**: Use MapStruct for mapping between entities and DTOs to reduce boilerplate code. (Could not use due to
   testing setup complications)
6. 