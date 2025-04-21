package com.das6.serversockets.repository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

import com.das6.serversockets.shared.StatusCode;


public class Repository {
    private static JdbcConnection dbChannel = JdbcConnection.getInstance();
    private final static String psLookForUserByCredentials = "SELECT NO_USER,NAME_USER,USERNAME,PASSWORD_USER,TYPE_ATENTION FROM USER_SYSTEM INNER JOIN TYPE_USER ON USER_SYSTEM.NO_TYPE_ATENTION = TYPE_USER.NO_TYPE WHERE USERNAME = ? AND PASSWORD_USER = ? AND STATUS_USER = TRUE";
    private final static String psCreateUser = "INSERT INTO USER_SYSTEM(NO_TYPE_ATENTION,NAME_USER,USERNAME,PASSWORD_USER) VALUES (?,?,?,?)";
    private final static String psDeleteUser = "UPDATE USER_SYSTEM SET STATUS_USER = FALSE WHERE NO_USER = ?";
    private final static String psLookForAllUsers = "SELECT * FROM USER_SYSTEM WHERE STATUS_USER = TRUE";
    private final static String psInserLogWithoutNull = "INSERT INTO LOG_SYSTEM(NO_USER_ATENTION,NO_TICKET,REF_CLIENT,TIME_ATENTION) VALUES (?,?,?,?)";
    private final static String psInsertLogWithNull = "INSERT INTO LOG_SYSTEM(NO_USER_ATENTION,NO_TICKET,TIME_ATENTION) VALUES (?,?,?)";
    private final static String psLookForAllLogs = "SELECT * FROM LOG_SYSTEM";

    public static JSONObject lookUpUserByCredentials(JSONObject credentials) {
        try(Connection conn = dbChannel.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(psLookForUserByCredentials);

            ps.setString(1, credentials.getString("username"));
            ps.setString(2, credentials.getString("password"));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JSONObject user = new JSONObject();

                user.put("no_user", rs.getInt("NO_USER"));
                user.put("name", rs.getString("NAME_USER"));
                user.put("username", rs.getString("USERNAME"));
                user.put("password", rs.getString("PASSWORD_USER"));
                user.put("type", rs.getString("TYPE_ATENTION"));

                return StatusCode.OK.toJsonWithData(user);
            }else {
                return StatusCode.UNAUTHORIZED.toJSON("Username or password is incorrect");
            }
        }catch(SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }

    public static JSONObject lookUpForAllUsers() {
        JSONArray users = new JSONArray();
        try(Connection conn = dbChannel.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(psLookForAllUsers);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                JSONObject user = new JSONObject();
                user.put("no_user", rs.getInt("NO_USER"));
                user.put("no_type_atention", rs.getInt("NO_TYPE_ATENTION"));
                user.put("name", rs.getString("NAME_USER"));
                user.put("username", rs.getString("USERNAME"));

                users.put(user);
            }

            if(users.isEmpty()) {
                return StatusCode.NOT_FOUND.toJSON("No users found");
            }

            return StatusCode.OK.toJsonWithData(users);

        }catch(SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }

    public static JSONObject createUser(JSONObject user) {
        try(Connection conn = dbChannel.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(psCreateUser);

            ps.setInt(1,user.getInt("no_type_atention"));
            ps.setString(2, user.getString("name"));
            ps.setString(3, user.getString("username"));
            ps.setString(4, user.getString("password"));

            int affectedRows = ps.executeUpdate();
            boolean status = affectedRows > 0;

            if(status) {
                return StatusCode.CREATED.toJSON("User created successfully");
            }

            return StatusCode.BAD_REQUEST.toJSON();

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }

    public static JSONObject deleteUser(JSONObject user) {
        try(Connection conn = dbChannel.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(psDeleteUser);

            ps.setInt(1,user.getInt("no_user"));
            int affectedRows = ps.executeUpdate();
            boolean status = affectedRows > 0;
            if(status) {
                return StatusCode.OK.toJSON("User deleted successfully");
            }

            return StatusCode.BAD_REQUEST.toJSON();
        }catch(SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }

    public static JSONObject insertLog(JSONObject log) {
        try(Connection conn = dbChannel.getConnection()) {
            PreparedStatement ps;

            if(log.get("ref_client") != JSONObject.NULL) {
                ps = conn.prepareStatement(psInserLogWithoutNull);

                ps.setInt(1,log.getInt("no_user"));
                ps.setString(2, log.getString("no_ticket"));
                ps.setString(3, log.getString("ref_client"));
                ps.setTime(4, Time.valueOf(log.getString("time_atention")));

            }else {
                ps = conn.prepareStatement(psInsertLogWithNull);
                ps.setInt(1,log.getInt("no_user"));
                ps.setString(2, log.getString("no_ticket"));
                ps.setTime(3, Time.valueOf(log.getString("time_atention")));
            }


            int affectedRows = ps.executeUpdate();
            boolean status = affectedRows > 0;
            if(status) {
                return StatusCode.CREATED.toJSON("Log created successfully");
            }

            return StatusCode.BAD_REQUEST.toJSON();
        }catch(SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }

    public static JSONObject lookUpAllLogs() {
        JSONArray logs = new JSONArray();
        try(Connection conn = dbChannel.getConnection()) {
            Statement ps = conn.createStatement();

            ResultSet rs = ps.executeQuery(psLookForAllLogs);

            while(rs.next()) {
                JSONObject log = new JSONObject();

                log.put("no_log", rs.getInt("NO_LOG"));
                log.put("no_user", rs.getInt("NO_USER_ATENTION"));
                log.put("no_ticket", rs.getString("NO_TICKET"));
                log.put("ref_client", rs.getString("REF_CLIENT"));
                log.put("date_atention", rs.getString("DATE_ATENTION"));
                log.put("time_atention", rs.getString("TIME_ATENTION"));

                logs.put(log);
            }

            if(logs.isEmpty()) {
                return StatusCode.NOT_FOUND.toJSON("No logs found");
            }

            return StatusCode.OK.toJsonWithData(logs);
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return StatusCode.INTERNAL_ERROR.toJSON();
        }
    }
}
