package ru.educationsystem.educationsystem.service;

import org.junit.jupiter.api.*;
import ru.educationsystem.educationsystem.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDao();
    }

    @Test
    public void testAuthenticateReturnFalse(){
       boolean result = userDao.authenticate("test@example.com", "password");
       assertFalse(result);
    }

    @Test
    public void testAuthenticateReturnTrue(){
        boolean result = userDao.authenticate("sidorov@example.com", "123");
        assertTrue(result);
    }

    @Test
    public void testAuthenticateEmailIsEmptyReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.authenticate("", "password"));
    }

    @Test
    public void testAuthenticateEmailIsNullReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.authenticate(null, "password"));
    }

    @Test
    public void testAuthenticatePasswordIsEmptyReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.authenticate("test@example.com", ""));
    }

    @Test
    public void testAuthenticatePasswordIsNullReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.authenticate("test@example.com", null));
    }

    @Test
    public void testFindByIdReturnTrue(){
        var result = userDao.findById(1);//если id существует
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByIdReturnFalse(){
        var result = userDao.findById(1000000);//если id не существует
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdByMinus1ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.findById(-1));
    }

    @Test
    public void testFindByIdBy0ReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.findById(0));
    }

    @Test
    public void testFindByEmailReturnNull(){
        var result = userDao.findByEmail("test@example.com");
        assertNull(result);
    }

    @Test
    public void testFindByEmailReturnUser(){
        var result = userDao.findByEmail("sidorov@example.com");
        assertNotNull(result);
    }

    @Test
    public void testFindByEmailEmailIsEmptyReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.findByEmail(""));
    }

    @Test
    public void testFindByEmailEmailIsNullReturnThrow(){
        assertThrows(IllegalArgumentException.class, () -> userDao.findByEmail(null));
    }

    @Test
    public void testGetAllUsersReturnList(){
        var result = userDao.findAll();
        assertNotNull(result);
    }
}