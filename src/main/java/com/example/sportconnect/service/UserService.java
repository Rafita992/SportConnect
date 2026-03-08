package com.example.sportconnect.service;

import com.example.sportconnect.dao.UserDAO;
import com.example.sportconnect.model.User;

import java.util.List;

public class UserService {

    // Instanciamos el DAO para tener acceso a la base de datos
    private final UserDAO userDAO = new UserDAO();

    /**
     * Lógica de autenticación.
     * @param email Email introducido en el formulario.
     * @param password Contraseña introducida.
     * @return El objeto User si es válido, null si no coincide o no existe.
     */
    public User login(String email, String password) {
        // 1. Pedimos al DAO que busque al usuario por email
        User user = userDAO.getUserByEmail(email);

        // 2. Verificamos si existe y si la contraseña coincide
        if (user != null && user.getPassword().equals(password)) {
            // Aquí podrías añadir más lógica en el futuro (ej. si la cuenta está bloqueada)
            return user;
        }

        // 3. Si algo falla, devolvemos null
        return null;
    }

    /**
     * Ejemplo de registro de usuario (Business Logic)
     */
    public boolean registerNewUser(User newUser) {
        // Comprobar si el email ya está en uso antes de guardar
        if (userDAO.getUserByEmail(newUser.getEmail()) == null) {
            userDAO.saveUser(newUser);
            return true;
        }
        return false;
    }

    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void saveUser(User user) {
        userDAO.saveUser(user);
    }
}