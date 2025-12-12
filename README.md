# 通讯录后端（Spring Boot）

## 技术栈
- Java 8
- Spring Boot 2.x（Web, Data JPA, Validation）
- MySQL 8.x
- Maven

## 快速开始
1) 配置数据库
- 新建数据库：`contacts_db`（或自定义）
- 修改 `src/main/resources/application.properties` 里的数据库连接信息
- 首次启动会自动运行 `schema.sql`

2) 启动服务
```bash
mvn spring-boot:run
```
服务默认运行在 `http://localhost:8080`

## API
- GET `/api/contacts` 列表
- GET `/api/contacts/{id}` 详情
- POST `/api/contacts` 新增
- PUT `/api/contacts/{id}` 修改
- DELETE `/api/contacts/{id}` 删除

## 目录结构
```
src/
  main/
    java/
      com/example/contacts/
        ContactsApplication.java
        controller/ContactController.java
        entity/Contact.java
        repository/ContactRepository.java
        service/ContactService.java
        dto/ContactRequest.java
    resources/
      application.properties
      schema.sql
pom.xml
```


