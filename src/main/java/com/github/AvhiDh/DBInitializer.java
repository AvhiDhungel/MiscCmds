package com.github.AvhiDh;

import com.github.AvhiDh.SqlUtilities.SqlDatabaseConnection;
import com.github.AvhiDh.SqlUtilities.SqlQueryBuilder;

public class DBInitializer {

    public static void execute(SqlDatabaseConnection conn) {
        initDeathSpot(conn);
    }

    private static void initDeathSpot(SqlDatabaseConnection conn) {
        SqlQueryBuilder sql = new SqlQueryBuilder();

        sql.appendLine("CREATE TABLE IF NOT EXISTS av_tblDeathSpot (");
        sql.appendLine("    fldId INT NOT NULL AUTO_INCREMENT,");
        sql.appendLine("    fldPlayerId VARCHAR(36) NOT NULL,");
        sql.appendLine("    fldXPos INT NOT NULL,");
        sql.appendLine("    fldYPos INT NOT NULL,");
        sql.appendLine("    fldZPos INT NOT NULL,");
        sql.appendLine("    fldWorld NVARCHAR(50) NOT NULL,");
        sql.appendLine("    fldDate DATETIME NOT NULL,");
        sql.appendLine("    PRIMARY KEY (fldId)");
        sql.appendLine(");");

        conn.executeNonQuery(sql);
        conn.close();
    }

}
