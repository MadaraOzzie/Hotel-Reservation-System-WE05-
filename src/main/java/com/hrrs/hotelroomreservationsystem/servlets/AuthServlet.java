package com.hrrs.hotelroomreservationsystem.servlets;

import com.hrrs.hotelroomreservationsystem.utils.FileRepository;
import com.hrrs.hotelroomreservationsystem.utils.Module;
import com.hrrs.hotelroomreservationsystem.utils.Passwords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public final class AuthServlet extends ApiServlet {
    @Override protected boolean publicAccess(HttpServletRequest r) {
        return "/login".equals(r.getPathInfo()) || "/logout".equals(r.getPathInfo()) || "/session".equals(r.getPathInfo()) || r.getPathInfo() == null;
    }

    @Override protected void doGet(HttpServletRequest r, HttpServletResponse s) throws IOException {
        String path = r.getPathInfo() == null ? "/" : r.getPathInfo();
        if (!"/session".equals(path)) throw new IllegalArgumentException("Unsupported auth route");
        HttpSession session = r.getSession(true);
        String csrf = Objects.toString(session.getAttribute("csrf"), "");
        if (csrf.isBlank()) {
            csrf = UUID.randomUUID().toString();
            session.setAttribute("csrf", csrf);
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("csrf", csrf);
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            response.put("user", null);
        } else {
            try {
                var row = new FileRepository(Module.STAFF).findById(((Number) userId).longValue());
                session.setAttribute("role", row.get("role"));
                session.setAttribute("name", row.get("name"));
                Map<String, Object> user = new LinkedHashMap<>();
                user.put("id", row.get("id"));
                user.put("name", row.get("name"));
                user.put("email", row.get("email"));
                user.put("role", row.get("role"));
                response.put("user", user);
            } catch (NoSuchElementException e) {
                session.invalidate();
                response.put("user", null);
            }
        }
        json(s, response);
    }

    @Override protected void doPost(HttpServletRequest r, HttpServletResponse s) throws IOException {
        String path = r.getPathInfo() == null ? "/" : r.getPathInfo();
        if ("/login".equals(path)) {
            Map<String, Object> data = body(r);
            String email = String.valueOf(data.getOrDefault("email", "")).trim().toLowerCase();
            String password = String.valueOf(data.getOrDefault("password", ""));
            try {
                for (var row : new FileRepository(Module.STAFF).findAll()) {
                    if (email.equalsIgnoreCase(String.valueOf(row.get("email")))) {
                        String stored = String.valueOf(row.get("password_hash"));
                        if (!Passwords.verify(password, stored)) {
                            throw new IllegalArgumentException("Invalid password");
                        }
                        HttpSession session = r.getSession(true);
                        session.setAttribute("userId", ((Number) row.get("id")).longValue());
                        session.setAttribute("role", row.get("role"));
                        session.setAttribute("name", row.get("name"));
                        session.setAttribute("csrf", java.util.UUID.randomUUID().toString());
                        Map<String, Object> user = new LinkedHashMap<>();
                        user.put("id", row.get("id"));
                        user.put("name", row.get("name"));
                        user.put("email", row.get("email"));
                        user.put("role", row.get("role"));
                        Map<String, Object> response = new LinkedHashMap<>();
                        response.put("csrf", session.getAttribute("csrf"));
                        response.put("user", user);
                        json(s, response);
                        return;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid login details");
            }
            throw new NoSuchElementException("Unknown account");
        }

        if ("/logout".equals(path)) {
            HttpSession session = r.getSession(false);
            if (session != null) session.invalidate();
            json(s, Map.of("logged_out", true));
            return;
        }

        throw new IllegalArgumentException("Unsupported auth route");
    }
}
