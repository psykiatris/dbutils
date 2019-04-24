package org.palczewski.connect;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatasourceUtil {

    private static final Logger logger=
            Logger.getLogger(DatasourceUtil.class.getName());

    public static DatasourceUtil getInstance() {
        return new DatasourceUtil();

    }

    private DatasourceUtil() {
        /*
        Don't allow default contstructor
         */
    }

    public class LoginResult(//User user, long tokenId) {
        // TODO: 4/23/19 Define an User type.
    }



}
