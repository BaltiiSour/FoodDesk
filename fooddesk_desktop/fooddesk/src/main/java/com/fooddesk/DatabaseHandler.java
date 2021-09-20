/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fooddesk;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.OracleTypes;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;


public class DatabaseHandler {
    private String DB_URL="jdbc:oracle:thin:@fooddeskdb_high?TNS_ADMIN=./Wallet_FoodDeskDB";
    private String DB_USER="admin";
    private String DB_PASSWORD="Fooddesk123.";
    private String CONN_FACTORY_CLASS_NAME="oracle.jdbc.pool.OracleDataSource";

    public DatabaseHandler() {
        
    }
    
    private Connection initConn() throws SQLException{
        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();

        // Set the connection factory first before all other properties
        pds.setConnectionFactoryClassName(this.CONN_FACTORY_CLASS_NAME);
        pds.setURL(this.DB_URL);
        pds.setUser(this.DB_USER);
        pds.setPassword(this.DB_PASSWORD);
        pds.setConnectionPoolName("JDBC_UCP_POOL");

        // Default is 0. Set the initial number of connections to be created
        // when UCP is started.
        pds.setInitialPoolSize(5);

        // Default is 0. Set the minimum number of connections
        // that is maintained by UCP at runtime.
        pds.setMinPoolSize(5);

        // Default is Integer.MAX_VALUE (2147483647). Set the maximum number of
        // connections allowed on the connection pool.
        pds.setMaxPoolSize(20);

        // Default is 30secs. Set the frequency in seconds to enforce the timeout
        // properties. Applies to inactiveConnectionTimeout(int secs),
        // AbandonedConnectionTimeout(secs)& TimeToLiveConnectionTimeout(int secs).
        // Range of valid values is 0 to Integer.MAX_VALUE. .
        pds.setTimeoutCheckInterval(5);

        // Default is 0. Set the maximum time, in seconds, that a
        // connection remains available in the connection pool.
        pds.setInactiveConnectionTimeout(10);

        // Set the JDBC connection properties after pool has been created
        Properties connProps = new Properties();
        connProps.setProperty("fixedString", "false");
        connProps.setProperty("remarksReporting", "false");
        connProps.setProperty("restrictGetTables", "false");
        connProps.setProperty("includeSynonyms", "false");
        connProps.setProperty("defaultNChar", "false");
        connProps.setProperty("AccumulateBatchResult", "false");

        // JDBC connection properties will be set on the provided
        // connection factory.
        pds.setConnectionProperties(connProps);
        return pds.getConnection();
    }
    
    public String getUserProfile(String user, String password) throws SQLException{
        CallableStatement cstmt = null;
        String call = "{call SP_LEE_USUARIO(?,?,?)}";
        try {
            Connection conn = this.initConn();
            cstmt = conn.prepareCall(call);
            /* IN PARAMS */
            cstmt.setString(1, user);
            cstmt.setString(2, password);
            /* OUT PARAMS */
            cstmt.registerOutParameter(3, OracleTypes.CURSOR);
            
            cstmt.execute();
            ResultSet result = (ResultSet) cstmt.getObject(3);
            String profile = null;
            
            while(result.next()){
                profile = result.getString("name");
            }
            
            return profile;
        }
        catch (SQLException e) {
            System.out.println(e);
            return null;
        }
        finally{
            cstmt.close();
        }
    }
    
}
