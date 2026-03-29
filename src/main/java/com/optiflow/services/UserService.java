package com.optiflow.services;

import com.optiflow.dao.UserDAO;
import com.optiflow.models.Employee;
import com.optiflow.models.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserService
{
    private UserDAO userDAO;
    private AuthService auth;

    UserService()
    {
        this.userDAO = new UserDAO();
        this.auth = new AuthService();
    }

    public boolean createUser(User user) throws SQLException
    {
        if (user == null)
            return false;

        if (!auth.isValidEmail(user.getEmail()))
        {
            System.out.println("Invalid email format");
            return false;
        }

        if (userDAO.getUserByEmail(user.getEmail()) != null)
        {
            System.out.println("Email already exists");
            return false;
        }

        if (!auth.validatePassword(user.getPasswordHash()))
        {
            System.out.println("Weak password");
            return false;
        }

        String hashedPassword = auth.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        return userDAO.addUser(user.getName(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    public User getUserById(int user_id) throws SQLException
    {
        if(user_id <= 0)
            return null;

        return userDAO.getUserById(user_id);
    }

    public User getUserByEmail(String email) throws SQLException
    {
        if(!auth.isValidEmail(email))
            return null;

        return userDAO.getUserByEmail(email);
    }

    public List<User> getAllUsers() throws SQLException
    {
        return userDAO.getAllUsers();
    }

    public boolean updateName(int user_id, String name) throws SQLException
    {
        if(user_id<=0 || name.isEmpty())
            return false;

        if(userDAO.updateName(user_id, name)!=1)
            return false;

        return true;
    }

    public boolean updateEmail(int user_id, String email) throws SQLException
    {
        if(user_id<=0 || email.isEmpty() || !auth.isValidEmail(email))
            return false;

        if(userDAO.updateEmail(user_id, email)!=1)
            return false;

        return true;
    }

    public boolean updatePassword(int user_id, String password) throws SQLException
    {
        if(user_id<=0 || password.isEmpty() || !auth.validatePassword(password))
            return false;

        String hashed_password = auth.hashPassword(password);

        if(userDAO.updatePassword(user_id, hashed_password)!=1)
            return false;

        return true;
    }

    public boolean updateRole(int user_id, String role) throws SQLException
    {
        if(user_id<=0 || role.isEmpty())
            return false;

        if(userDAO.updateRole(user_id, role)!=1)
            return false;

        return true;
    }

    public boolean deleteUser(int user_id) throws SQLException
    {
        if(user_id <= 0)
            return false;

        return userDAO.deleteUser(user_id);
    }

    public List<User> getUsersByRole(String role) throws SQLException
    {
        if(role==null || role.isEmpty())
            return null;

        return userDAO.getUsersByRole(role);
    }

    public String exportUsersToCSV() throws Exception
    {
        List<User> users = userDAO.getAllUsers();

        String fileName = "users_" + LocalDate.now() + ".csv";

        FileWriter writer = new FileWriter(fileName);

        writer.append("User-ID,Name,Email,Password Hash,Role\n");

        for (User u : users)
        {
            writer.append(u.getUserId() + ",")
                    .append(u.getName() + ",")
                    .append(u.getEmail() + ",")
                    .append(u.getPasswordHash() + ",")
                    .append(u.getRole() + "\n");
        }

        writer.flush();
        writer.close();

        return fileName;
    }

    public String exportUsersToExcel() throws Exception
    {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Password Hash");
        header.createCell(4).setCellValue("Role");

        int rowNum = 1;

        for(User u : userDAO.getAllUsers()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getUserId());
            row.createCell(1).setCellValue(u.getName());
            row.createCell(2).setCellValue(u.getEmail());
            row.createCell(3).setCellValue(u.getPasswordHash());
            row.createCell(4).setCellValue(u.getRole());
        }

        FileOutputStream fileOut = new FileOutputStream("users.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        return "";
    }
}
