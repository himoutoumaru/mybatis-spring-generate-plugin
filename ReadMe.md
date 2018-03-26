# 概述
本插件用于为MyBaits增加Spring的注解和把数据库的备注作为Swagger的注解添加到自动生成的MyBatis文件中

# 使用方式

Maven添加插件
```
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>5.1.45</version>
                    </dependency>
                    <dependency>
                        <groupId>org.guiltycrown</groupId>
                        <artifactId>mybatis-spring-generate-plugin</artifactId>
                        <version>1.0.0</version>
                    </dependency>
                </dependencies>
            </plugin>
```

编写`generatorConfig.xml`文件中添加如下参数
```
<generatorConfiguration>
    <context id="context1" targetRuntime="MyBatis3">
    <plugin type="org.supercall.MybatisPlugin">
                <property name="schemaURL" value="jdbc:mysql://{MySQL的IP和端口}/information_schema"/>
                <property name="schema" value="目标数据库"/>
    </plugin>
    .....
```

运行生成命令即可
```
mvn mybatis-generator:generate
```

#更新说明
##1.0.0
* 为实体类加入了Swagger Model的注解，注解的描述从数据库中获取
* 为Mapper加入了Repository注解