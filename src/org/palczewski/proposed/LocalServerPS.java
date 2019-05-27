package org.palczewski.proposed;


import java.io.Reader;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.SQLException;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.jdbc.MysqlDefs;
import com.mysql.cj.jdbc.exceptions.SQLError;

import com.mysql.cj.jdbc.ServerPreparedStatement;
import com.mysql.cj.ServerPreparedQueryBindValue;

public class LocalServerPS extends ServerPreparedStatement {

    public LocalServerPS(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        super(conn, sql, catalog, resultSetType, resultSetConcurrency);
    }



    /**
     * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
     */
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        // can't take if characterEncoding isn't utf8
        if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
            throw SQLError.createSQLException("Can not call setNCharacterStream() when connection character set isn't UTF-8", getExceptionInterceptor());
        }

        checkClosed();

        if (reader == null) {
            setNull(parameterIndex, java.sql.Types.BINARY);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = reader;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = length;
            } else {
                binding.bindLength = -1;
            }
        }
    }

    /**
     * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
     */
    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        setNClob(parameterIndex, x.getCharacterStream(), this.connection.getUseStreamLengthsInPrepStmts() ? x.length() : -1);
    }

    /**
     * JDBC 4.0 Set a NCLOB parameter.
     *
     * @param parameterIndex
     *            the first parameter is 1, the second is 2, ...
     * @param reader
     *            the java reader which contains the UNICODE data
     * @param length
     *            the number of characters in the stream
     *
     * @throws SQLException
     *             if a database error occurs
     */
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // can't take if characterEncoding isn't utf8
        if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
            throw SQLError.createSQLException("Can not call setNClob() when connection character set isn't UTF-8", getExceptionInterceptor());
        }

        checkClosed();

        if (reader == null) {
            setNull(parameterIndex, java.sql.Types.NCLOB);
        } else {
            BindValue binding = getBinding(parameterIndex, true);
            setType(binding, MysqlDefs.FIELD_TYPE_BLOB);

            binding.value = reader;
            binding.isNull = false;
            binding.isLongData = true;

            if (this.connection.getUseStreamLengthsInPrepStmts()) {
                binding.bindLength = length;
            } else {
                binding.bindLength = -1;
            }
        }
    }

    /**
     * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
     */
    public void setNString(int parameterIndex, String x) throws SQLException {
        if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
            setString(parameterIndex, x);
        } else {
            throw SQLError.createSQLException("Can not call setNString() when connection character set isn't UTF-8", getExceptionInterceptor());
        }
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        LocalServerPS.setSQLXML(this, parameterIndex, xmlObject);
    }
}
