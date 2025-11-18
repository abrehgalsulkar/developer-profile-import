<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="fragments/head.jspf" %>
  <title>Admin Login</title>
  <style>
    body {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: radial-gradient(circle at top, rgba(67, 56, 202, 0.15), rgba(15, 23, 42, 0.1));
    }

    .auth-card {
      width: 100%;
      max-width: 420px;
      background-color: #fff;
      border-radius: 24px;
      box-shadow: 0 20px 50px rgba(15, 23, 42, 0.15);
      padding: 2.5rem;
    }

    .auth-card h1 {
      font-size: 1.8rem;
      font-weight: 700;
      margin-bottom: 0.25rem;
      color: #0f172a;
    }

    .auth-card p {
      color: #475569;
      margin-bottom: 1.5rem;
    }

    .auth-card .form-label {
      font-weight: 600;
      color: #0f172a;
    }

    .auth-card .form-control {
      border-radius: 14px;
      padding: 0.75rem 1rem;
    }

    .auth-card .btn-primary {
      border-radius: 999px;
      padding: 0.75rem;
      font-weight: 600;
    }

    .auth-card footer {
      margin-top: 1.5rem;
      font-size: 0.85rem;
      color: #94a3b8;
    }
  </style>
</head>
<body>
  <main class="auth-card">
    <h1>Admin login</h1>
    <form method="post" action="<c:url value='/login' />" novalidate>
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input
          type="text"
          class="form-control"
          id="username"
          name="username"
          autocomplete="username"
          required
          autofocus
        >
      </div>
      <div class="mb-4">
        <label for="password" class="form-label">Password</label>
        <input
          type="password"
          class="form-control"
          id="password"
          name="password"
          autocomplete="current-password"
          required
        >
      </div>
      <button type="submit" class="btn btn-primary w-100">Sign in</button>
    </form>
  </main>

  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <script>
    document.addEventListener('DOMContentLoaded', function () {
      const params = new URLSearchParams(window.location.search);
      if (params.has('error')) {
        Swal.fire({
          icon: 'error',
          title: 'Login failed',
          text: 'Invalid username or password. Please try again.',
          confirmButtonColor: '#4338ca'
        });
      } else if (params.has('logout')) {
        Swal.fire({
          icon: 'success',
          title: 'Signed out',
          text: 'You have been logged out successfully.',
          confirmButtonColor: '#4338ca'
        });
      }
    });
  </script>
</body>
</html>
