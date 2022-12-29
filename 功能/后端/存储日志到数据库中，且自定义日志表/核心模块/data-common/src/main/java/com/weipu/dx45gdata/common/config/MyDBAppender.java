///**
// * Logback: the reliable, generic, fast and flexible logging framework.
// * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
// *
// * This program and the accompanying materials are dual-licensed under
// * either the terms of the Eclipse Public License v1.0 as published by
// * the Eclipse Foundation
// *
// *   or (per the licensee's choosing)
// *
// * under the terms of the GNU Lesser General Public License version 2.1
// * as published by the Free Software Foundation.
// */
package com.weipu.dx45gdata.common.config;
//
//import static ch.qos.logback.core.db.DBHelper.closeStatement;
//
//import java.lang.reflect.Method;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import ch.qos.logback.classic.db.DBHelper;
//import ch.qos.logback.classic.db.names.ColumnName;
//import ch.qos.logback.classic.db.names.DBNameResolver;
//import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
//import ch.qos.logback.classic.db.names.TableName;
//import ch.qos.logback.classic.spi.*;
//import ch.qos.logback.core.CoreConstants;

import ch.qos.logback.classic.db.names.ColumnName;
import ch.qos.logback.classic.db.names.DBNameResolver;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.classic.db.names.TableName;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.db.DBAppenderBase;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * The DBAppender inserts logging events into three database tables in a format
 * independent of the Java programming language.
 *
 * For more information about this appender, please refer to the online manual
 * at http://logback.qos.ch/manual/appenders.html#DBAppender
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Ray DeCampo
 * @author S&eacute;bastien Pennec
 */
@Slf4j
public class MyDBAppender extends DBAppenderBase<ILoggingEvent> {
//    protected String insertPropertiesSQL;
//    protected String insertExceptionSQL;
    protected String insertSQL;
    protected static final Method GET_GENERATED_KEYS_METHOD;

    private DBNameResolver dbNameResolver;

    static final int PX_NAME_INDEX=1;
    static final int  INFO_INDEX=2;
    static final int  STATUS_INDEX=3;
    static final int SQL_INDEX=4;
    static final int SQL_COUNT_INDEX=5;
    static final int START_TIME_INDEX=6;
    static final int END_TIME_INDEX=7;
    static final int ARG0_INDEX = 5;
    static final int CALLER_FILENAME_INDEX = 6;
    static final int CALLER_CLASS_INDEX = 7;
    static final int CALLER_METHOD_INDEX = 8;
    static final int CALLER_LINE_INDEX = 9;
    static final int EVENT_ID_INDEX = 10;

    static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

    static {
        // PreparedStatement.getGeneratedKeys() method was added in JDK 1.4
        Method getGeneratedKeysMethod;
        try {
            // the
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }

    public void setDbNameResolver(DBNameResolver dbNameResolver) {
        this.dbNameResolver = dbNameResolver;
    }

    @Override
    public void start() {
        if (dbNameResolver == null)
            dbNameResolver = new DefaultDBNameResolver();
//        insertExceptionSQL = buildInsertExceptionSQL(dbNameResolver);
//        insertPropertiesSQL = buildInsertPropertiesSQL(dbNameResolver);
        insertSQL = buildInsertSQL(dbNameResolver);
        super.start();
    }

    @Override
    protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement) throws Throwable {


        String info = bindLoggingEventWithInsertStatement(insertStatement, event);
        if(info.isEmpty()){
            return;
        }
        int updateCount = insertStatement.executeUpdate();
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
//        bindLoggingEventArgumentsWithPreparedStatement(insertStatement, event.getArgumentArray());
        // This is expensive... should we do it every time?
//        bindCallerDataWithPreparedStatement(insertStatement, event.getCallerData());



    }

     protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {
//        if(event.getLevel().levelStr.equals("ERROR")&&event.getLoggerName().equals("jdbc.sqlonly")){
//            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM log WHERE event_id = ?");
//            preparedStatement.setLong(1,eventId-1);
//            int i = preparedStatement.executeUpdate( );
//            if(i!=1){
//                addWarn("Failed to Delete loggingEvent");
//            };
//
//        }

//        Map<String, String> mergedMap = mergePropertyMaps(event);
//        insertProperties(mergedMap, connection, eventId);
//
//        if (event.getThrowableProxy() != null) {
//            insertThrowable(event.getThrowableProxy(), connection, eventId);
//
//        }
    }

    String bindLoggingEventWithInsertStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        String px_name="";
        String info="";
        String status="";
        String sql="";
        Integer sql_count=0;
        Timestamp start_time = null;
        Timestamp end_time=null;

        //px_name
        px_name=event.getMDCPropertyMap().get("px_name");
        //status
        status=event.getLevel( ).levelStr.equals("ERROR")?"failed":"success";
        //info sql
        if(event.getLoggerName().equals("jdbc.sqlonly")){
            //记录jdbc.sqlonly ERROR级别信息  event.getThrowableProxy()不为空
            if(event.getThrowableProxy()!=null){
                IThrowableProxy throwableProxy = event.getThrowableProxy( );
                info=throwableProxy.getClassName()+": "+throwableProxy.getMessage();
                sql = event.getMessage( ).substring(event.getMessage( ).indexOf("I"));
            }
        }
        else{
            // 主动抛出异常不是代理异常
            if(event.getThrowableProxy()!=null){
                info=ThrowableProxyUtil.asString(event.getThrowableProxy( )).substring(0,255);
            }else{
                //拦截了 采集成功的日志
                info=event.getMessage();
            }
        }
        //sql_count
        if (event.getMDCPropertyMap().get("sql_count")!=null){
            sql_count= Integer.valueOf(event.getMDCPropertyMap().get("sql_count"));
        }
        //start_time
        if(event.getMDCPropertyMap().get("start_time")!=null){
            start_time=new Timestamp(Long.valueOf(event.getMDCPropertyMap().get("start_time")));
        }

        //end_time
        if(event.getMDCPropertyMap().get("end_time")!=null){
            end_time=new Timestamp(Long.valueOf(event.getMDCPropertyMap().get("end_time")));
        }

        stmt.setString(PX_NAME_INDEX,px_name);
        stmt.setString(INFO_INDEX,info);
        stmt.setString(STATUS_INDEX,status);
        stmt.setString(SQL_INDEX,sql);
        stmt.setInt(SQL_COUNT_INDEX,sql_count);
        stmt.setTimestamp(START_TIME_INDEX,start_time);
        stmt.setTimestamp(END_TIME_INDEX,end_time);
        return info;
//        StringBuffer info=new StringBuffer( );
//        String sqlStr=null;
//        stmt.setTimestamp(TIMESTMP_INDEX, new Timestamp(event.getTimeStamp()));
//        stmt.setString(PROGRAM_INDEX,event.getMDCPropertyMap().get("program"));
//        info.append(event.getLevel( ).levelStr.equals("ERROR")?"failed":"success");
//        info.append(" ");
//        if(event.getLoggerName().equals("jdbc.sqlonly")){
//            if(event.getLevel().levelStr.equals("ERROR")){
//                sqlStr = event.getMessage( ).substring(event.getMessage( ).indexOf("I"));
//                stmt.setString(STR_SQL_INDEX,sqlStr);
////                stmt.setInt(RECORD_COUNT_INDEX,1);
//            }else {
//                stmt.setString(STR_SQL_INDEX,event.getMessage());
////                stmt.setInt(RECORD_COUNT_INDEX,1);
//            }
//            if(event.getThrowableProxy()!=null){
//
//                info.append(ThrowableProxyUtil.asString(event.getThrowableProxy( )));
//                stmt.setString(INFO_INDEX,info.toString().substring(0,255));
//            }else{
//                stmt.setString(INFO_INDEX,info.toString());
//            }
//        }else{
//            info=new StringBuffer(  );
//            stmt.setString(STR_SQL_INDEX,sqlStr);
//            if(event.getThrowableProxy()!=null){
//                info.append(ThrowableProxyUtil.asString(event.getThrowableProxy( )));
//                stmt.setString(INFO_INDEX,info.toString().substring(0,255));
//            }else{
//                stmt.setString(INFO_INDEX,info.append(event.getMessage()).toString());
//            }
//        }
//        stmt.setInt(RECORD_COUNT_INDEX,1);

//        if(event.getThrowableProxy()!=null){
//            info.append(" ");
//            info.append(ThrowableProxyUtil.asString(event.getThrowableProxy( )));
//            stmt.setString(INFO_INDEX,info.toString().substring(0,255));
//        }else{
//            stmt.setString(INFO_INDEX,info.append().toString());
//        }

//        MDC.clear();
    }

    void bindLoggingEventArgumentsWithPreparedStatement(PreparedStatement stmt, Object[] argArray) throws SQLException {

        int arrayLen = argArray != null ? argArray.length : 0;

        for (int i = 0; i < arrayLen && i < 4; i++) {
            stmt.setString(ARG0_INDEX + i, asStringTruncatedTo254(argArray[i]));
        }
        if (arrayLen < 4) {
            for (int i = arrayLen; i < 4; i++) {
                stmt.setString(ARG0_INDEX + i, null);
            }
        }
    }

    String asStringTruncatedTo254(Object o) {
        String s = null;
        if (o != null) {
            s = o.toString();
        }

        if (s == null) {
            return null;
        }
        if (s.length() <= 254) {
            return s;
        } else {
            return s.substring(0, 254);
        }
    }

    void bindCallerDataWithPreparedStatement(PreparedStatement stmt, StackTraceElement[] callerDataArray) throws SQLException {

        StackTraceElement caller = extractFirstCaller(callerDataArray);

        stmt.setString(CALLER_FILENAME_INDEX, caller.getFileName());
        stmt.setString(CALLER_CLASS_INDEX, caller.getClassName());
        stmt.setString(CALLER_METHOD_INDEX, caller.getMethodName());
        stmt.setString(CALLER_LINE_INDEX, Integer.toString(caller.getLineNumber()));
    }

    private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
        StackTraceElement caller = EMPTY_CALLER_DATA;
        if (hasAtLeastOneNonNullElement(callerDataArray))
            caller = callerDataArray[0];
        return caller;
    }

    private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
        return callerDataArray != null && callerDataArray.length > 0 && callerDataArray[0] != null;
    }

    Map<String, String> mergePropertyMaps(ILoggingEvent event) {
        Map<String, String> mergedMap = new HashMap<String, String>();
        // we add the context properties first, then the event properties, since
        // we consider that event-specific properties should have priority over
        // context-wide properties.
        Map<String, String> loggerContextMap = event.getLoggerContextVO().getPropertyMap();
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (loggerContextMap != null) {
            mergedMap.putAll(loggerContextMap);
        }
        if (mdcMap != null) {
            mergedMap.putAll(mdcMap);
        }

        return mergedMap;
    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }

//    protected void insertProperties(Map<String, String> mergedMap, Connection connection, long eventId) throws SQLException {
//        Set<String> propertiesKeys = mergedMap.keySet();
//        if (propertiesKeys.size() > 0) {
//            PreparedStatement insertPropertiesStatement = null;
//            try {
//                insertPropertiesStatement = connection.prepareStatement(insertPropertiesSQL);
//
//                for (String key : propertiesKeys) {
//                    String value = mergedMap.get(key);
//
//                    insertPropertiesStatement.setLong(1, eventId);
//                    insertPropertiesStatement.setString(2, key);
//                    insertPropertiesStatement.setString(3, value);
//
//                    if (cnxSupportsBatchUpdates) {
//                        insertPropertiesStatement.addBatch();
//                    } else {
//                        insertPropertiesStatement.execute();
//                    }
//                }
//
//                if (cnxSupportsBatchUpdates) {
//                    insertPropertiesStatement.executeBatch();
//                }
//            } finally {
//                closeStatement(insertPropertiesStatement);
//            }
//        }
//    }

    /**
     * Add an exception statement either as a batch or execute immediately if
     * batch updates are not supported.
     */
    void updateExceptionStatement(PreparedStatement exceptionStatement, String txt, short i, long eventId) throws SQLException {
        exceptionStatement.setLong(1, eventId);
        exceptionStatement.setShort(2, i);
        exceptionStatement.setString(3, txt);
        if (cnxSupportsBatchUpdates) {
            exceptionStatement.addBatch();
        } else {
            exceptionStatement.execute();
        }
    }

    short buildExceptionStatement(IThrowableProxy tp, short baseIndex, PreparedStatement insertExceptionStatement, long eventId) throws SQLException {

        StringBuilder buf = new StringBuilder();
        ThrowableProxyUtil.subjoinFirstLine(buf, tp);
        updateExceptionStatement(insertExceptionStatement, buf.toString(), baseIndex++, eventId);

        int commonFrames = tp.getCommonFrames();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        for (int i = 0; i < stepArray.length - commonFrames; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(CoreConstants.TAB);
            ThrowableProxyUtil.subjoinSTEP(sb, stepArray[i]);
            updateExceptionStatement(insertExceptionStatement, sb.toString(), baseIndex++, eventId);
        }

        if (commonFrames > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(CoreConstants.TAB).append("... ").append(commonFrames).append(" common frames omitted");
            updateExceptionStatement(insertExceptionStatement, sb.toString(), baseIndex++, eventId);
        }

        return baseIndex;
    }

//    protected void insertThrowable(IThrowableProxy tp, Connection connection, long eventId) throws SQLException {
//
//        PreparedStatement exceptionStatement = null;
//        try {
//            exceptionStatement = connection.prepareStatement(insertExceptionSQL);
//
//            short baseIndex = 0;
//            while (tp != null) {
//                baseIndex = buildExceptionStatement(tp, baseIndex, exceptionStatement, eventId);
//                tp = tp.getCause();
//            }
//
//            if (cnxSupportsBatchUpdates) {
//                exceptionStatement.executeBatch();
//            }
//        } finally {
//            closeStatement(exceptionStatement);
//        }
//
//    }
    static String buildInsertPropertiesSQL(DBNameResolver dbNameResolver) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT_PROPERTY)).append(" (");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.MAPPED_KEY)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.MAPPED_VALUE)).append(") ");
        sqlBuilder.append("VALUES (?, ?, ?)");
        return sqlBuilder.toString();
    }

    static String buildInsertExceptionSQL(DBNameResolver dbNameResolver) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT_EXCEPTION)).append(" (");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.I)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.TRACE_LINE)).append(") ");
        sqlBuilder.append("VALUES (?, ?, ?)");
        return sqlBuilder.toString();
    }

    static String buildInsertSQL(DBNameResolver dbNameResolver) {
        StringBuilder sqlBuilder = new StringBuilder(
                "INSERT INTO " +
                        " log(px_name,info,log_status,s_sql,sql_count,start_time,end_time) "+
                        "VALUES (?,?,?,?,?,?,?)"
        );
        return sqlBuilder.toString();
    }
}

