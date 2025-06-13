# 📄 API Documentation

## General Notes
- Base URL: http://localhost:8080/api
- Semua endpoint (kecuali `/auth/register` & `/auth/login`) membutuhkan **Authorization Header**:

Authorization: Bearer <JWT_TOKEN>

---

1️⃣ Auth

POST `/auth/register`
Request Body
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@gmail.com",
    "password": "password123"
}
Response
"<JWT_TOKEN>"



POST `/auth/login`

Request Body
```json
{
    "email": "johndoe@gmail.com",
    "password": "password123"
}
Response
"<JWT_TOKEN>"



POST /auth/logout
Headers
Authorization: Bearer <JWT_TOKEN>
Response
"Logout successful"


---


2️⃣ Journal

GET /journal?date=YYYY-MM-DD
Headers
Authorization: Bearer <JWT_TOKEN>
Response
{
    "date": "2025-06-12",
    "content": "Today I felt good!"
}


POST /journal
Headers
Authorization: Bearer <JWT_TOKEN>

Request Body
{
    "date": "2025-06-12",
    "content": "Today I felt good!"
}
Response
"Journal saved successfully"



DELETE /journal?date=YYYY-MM-DD
Headers
Authorization: Bearer <JWT_TOKEN>
Response
"Journal deleted successfully"


---


3️⃣ Mood Tracker

GET /mood?date=YYYY-MM-DD
Headers
Authorization: Bearer <JWT_TOKEN>
Response
{
    "date": "2025-06-12",
    "mood": "Happy"
}



POST /mood
Headers
Authorization: Bearer <JWT_TOKEN>
Request Body
{
    "date": "2025-06-12",
    "mood": "Happy"
}
Response
"Mood saved successfully"



DELETE /mood?date=YYYY-MM-DD
Authorization: Bearer <JWT_TOKEN>
Response
"Mood deleted successfully"


---


Notes
- Semua tanggal dalam format: "YYYY-MM-DD".
- Semua waktu dalam format ISO 8601.
- Semua endpoint membutuhkan Authorization Header, kecuali /auth/register & /auth/login.

Tips
- Gunakan /auth/login untuk mendapatkan token JWT.
- Simpan token → gunakan di semua endpoint lain dengan header:
- Authorization: Bearer <JWT_TOKEN>


---


API SUMMARY

🔐 1️⃣ Authentication API
Digunakan untuk proses register, login, logout.
POST /auth/register → register user baru → return JWT token
POST /auth/login → login user → return JWT token
POST /auth/logout → logout user (client-side logout)

JWT token digunakan untuk autentikasi semua API lain.

📓 2️⃣ Journal API
Digunakan untuk fitur jurnal pribadi user.
GET /journal?date=YYYY-MM-DD → ambil jurnal untuk tanggal tertentu
POST /journal → simpan jurnal untuk tanggal tertentu
DELETE /journal?date=YYYY-MM-DD → hapus jurnal untuk tanggal tertentu

😊 3️⃣ Mood Tracker API
Digunakan untuk fitur mood tracker harian.
GET /mood?date=YYYY-MM-DD → ambil mood untuk tanggal tertentu
POST /mood → simpan mood untuk tanggal tertentu
DELETE /mood?date=YYYY-MM-DD → hapus mood untuk tanggal tertentu



