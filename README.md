# ♻️ Garbigo

<p align="center">
  <img src="https://github.com/graham218.png" width="120" style="border-radius:50%" alt="Project Owner" />
</p>

<p align="center">
  <strong>A modern, scalable garbage collection & waste management platform</strong><br />
  Built with Spring Boot Microservices, Flutter, Docker & Kubernetes
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-in%20development-yellow" />
  <img src="https://img.shields.io/badge/backend-Spring%20Boot-brightgreen" />
  <img src="https://img.shields.io/badge/frontend-Flutter-blue" />
  <img src="https://img.shields.io/badge/database-MongoDB-success" />
  <img src="https://img.shields.io/badge/cache-Redis-red" />
  <img src="https://img.shields.io/badge/container-Docker-2496ED" />
  <img src="https://img.shields.io/badge/orchestration-Kubernetes-326CE5" />
</p>

---

## 🌍 About Garbigo

**Garbigo** is a smart garbage collection and waste management application designed to modernize how waste is requested, collected, tracked, and managed.

The platform connects **clients**, **waste collectors/sellers**, and **administrators** through dedicated dashboards, enabling real-time scheduling, payments, analytics, and operational transparency.

Garbigo is built with **scalability, cloud-native architecture, and performance** in mind.

---

## ✨ Key Features

### 👤 User Management & Authentication
- Secure authentication & authorization
- Role-based access control (Admin, Client, Seller)
- JWT + OAuth-ready architecture
- Profile management with avatars stored in **Cloudinary**

### 🗑️ Garbage Collection Workflow
- Request garbage pickup
- Assign collectors automatically or manually
- Track pickup status in real time
- History & audit logs

### 💳 Payments & Billing
- Secure payment processing microservice
- Transaction history
- Invoicing & receipts

### 📱 Flutter App
- Android, iOS & Web
- Modern responsive UI
- Real-time updates

---

## 🧱 System Architecture

```
Flutter App
    |
API Gateway
    |
-------------------------------------------------
| Auth | Users | Collection | Payments | Notify |
-------------------------------------------------
    |
 MongoDB + Redis
```

---

## 🧰 Tech Stack

**Backend**
- Java 17
- Spring Boot Microservices
- Spring Security + JWT
- MongoDB
- Redis

**Frontend**
- Flutter (Mobile & Web)

**Storage**
- Cloudinary (Profile Images)
- Firebase Storage (Files & Documents)

**DevOps**
- Docker
- Kubernetes
- CI/CD Ready

---

## 👨‍💻 Project Owner

**Bill Graham**  
GitHub: https://github.com/graham218  
Title: *Peacemaker* ✌️

---

## 🛣️ Roadmap
- Authentication system
- Payments integration
- Real-time tracking
- Notifications
- AI route optimization

---

## 📄 License

This project is licensed under the **GNU General Public License (GPL)**.

Any derivative work must remain open-source under the same license.

---

♻️ **Garbigo – Building cleaner cities through technology**
