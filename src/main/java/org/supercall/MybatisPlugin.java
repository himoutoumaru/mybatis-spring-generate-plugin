package org.supercall;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.JDBCConnectionConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MybatisPlugin extends PluginAdapter {

    Connection connection;

    @Override
    public boolean validate(List<String> list) {
        try {
            JDBCConnectionConfiguration jdbcConfig = this.getContext().getJdbcConnectionConfiguration();
            Class.forName(jdbcConfig.getDriverClass());
            connection = DriverManager.getConnection(
                    properties.getProperty("schemaURL"),
                    jdbcConfig.getUserId(),
                    jdbcConfig.getPassword()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addAnnotation("@Repository");
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format(
                    "select TABLE_COMMENT from TABLES where TABLE_SCHEMA='%s' and TABLE_NAME='%s'"
                    , properties.getProperty("schema"),
                    introspectedTable.getTableConfiguration().getTableName()));
            topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
            topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
            rs.next();
            topLevelClass.addAnnotation(String.format("@ApiModel(value = \"%s\")", rs.getString("TABLE_COMMENT")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format(
                    "select COLUMN_COMMENT from information_schema.COLUMNS where TABLE_SCHEMA='%s' " +
                            "and TABLE_NAME='%s' and COLUMN_NAME='%s'"
                    , properties.getProperty("schema"),
                    introspectedTable.getTableConfiguration().getTableName(),
                    introspectedColumn.getActualColumnName()));
            rs.next();
            field.addAnnotation(String.format("@ApiModelProperty(value = \"%s\")", rs.getString("COLUMN_COMMENT")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        return super.contextGenerateAdditionalJavaFiles(introspectedTable);
    }


}
