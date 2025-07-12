# 🎓 Graduation Project – Full Stack Application

This is a full-stack web application for managing students, teachers, theses, and defenses. Built using Spring Boot (Java) and React, with JWT-based authentication and Dockerized deployment.

---

## 🚀 Tech Stack

- **Frontend**: React, Bootstrap
- **Backend**: Spring Boot (Java)
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Testing**: Cypress (E2E)
- **CI/CD**: Jenkins
- **Containerization**: Docker

---

## 📦 Features

- 🔐 Secure login with JWT (Student & Teacher roles)
- 📄 CRUD operations for Students, Teachers, Theses, and Defenses
- 📚 Thesis status workflow (Submitted → Approved → Defended)
- 🔎 Filtering, sorting, and pagination
- 🎯 Role-based access to routes (via frontend and backend)
- 🧪 Cypress end-to-end tests
- 📦 Dockerized backend and frontend

---

## 🐳 Docker Setup (Recommended)

> You must have [Docker](https://www.docker.com/) installed.

### Run backend + frontend together:

```bash
docker-compose up --build