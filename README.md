# Spring Boot連接資料庫範例(Spring Data JPA+PostgreSQL)
![Spring Data JPA](https://imgur.com/BJSNM9b.png)

> 本篇介紹如何用Spring Data JPA(Java Persistence API)連接資料庫，資料庫使用PostgreSQL，將Table資料顯示在網頁上，建立一個將POJO存在資料庫的應用，實現簡易的**CRUD**，並附上GitHub連結。:raising_hand: 

[TOC]
## Preface
### What Is JPA?:confused: 
JPA 是一個基於Object/Relational映射的標準規範，所謂規範即只定義標準規則（如註解、接口），不提供實現，而使用者只需按照規範中定義的方式來使用，而不用和軟件提供商的實現打交道。

### Spring Data JPA
Spring Data JPA 是 Spring 根據 ORM 框架和 JPA 規範而封裝的 JPA 應用框架，目的是降低存取資料層的工作量，讓開發人員只需寫出 repository 的介面，而 Spring 自動幫你實作其功能。
[Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

---

## :memo:What You Need?

- IDE：Your Favorite IDE
- Database：[PostGreSQL] (免安裝版)
- Java：JDK11
- [Maven 3.2+] or [Gradle 4+]

:::info
:bulb:**Hint:** PostGreSQL為免安裝版，安裝前請閱讀說明文件進行初始化設定，也可以使用其他資料庫，例如：MySQL。
:::

---

### :clipboard:Table Schema 

建立Employee表格於資料庫中，若非PostGreSQL需調整SQL。:arrow_down: 

```sql=
CREATE SCHEMA employee;

CREATE SEQUENCE employee.employee_emp_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;
    
CREATE TABLE employee.employee (
    "emp_id" INT NOT NULL DEFAULT nextval('employee.employee_emp_id_seq'::regclass), 
	"first_name" VARCHAR(30) NULL,
	"last_name" VARCHAR(30) NULL,
	"email" VARCHAR(500) NULL,
	"mobile_no" VARCHAR(15) NULL,
	"create_time" timestamp without time zone NULL,
	"update_time" timestamp without time zone NULL,
	PRIMARY KEY ("emp_id")
);

COMMENT ON COLUMN "employee"."emp_id" IS '流水號';
COMMENT ON COLUMN "employee"."first_name" IS '名';
COMMENT ON COLUMN "employee"."last_name" IS '姓';
COMMENT ON COLUMN "employee"."email" IS '信箱';
COMMENT ON COLUMN "employee"."mobile_no" IS '手機';
COMMENT ON COLUMN "employee"."create_time" IS '建立時間';
COMMENT ON COLUMN "employee"."update_time" IS '更新時間';
```

[PostGreSQL]: https://github.com/pgsql-tw/portable-pgsql
[Maven 3.2+]:https://maven.apache.org/download.cgi
[Gradle 4+]:https://gradle.org/install/

---

## Starting With Spring Initializr:rocket:

你可以使用Spring提供的「[spring initializr]」，先進行初始化專案，提供應用程序所需的所有依賴項目，並完成大部分設置。:arrow_down:

1. 進入[spring initializr]
2. 選擇 Gradle 或 Maven 以及您要使用的語言，此次選用的是Java11。
3. 加入相關依賴選擇：==Spring Data JPA==、==PostgreSQL Driver==、==Spring Boot DevTools==、==Lombok==、==Spring Web==
![dependencies](https://i.imgur.com/wXbDEOL.png)
4. 點擊**Generate**
5. 下載生成ZIP檔案

:::info
:bulb:**Hint:** 若你的IDE有Spring Initializr，則可透過IDE完成設定，或可從GitHub上fork並在IDE中開啟。:smiley_cat: 
:::

[spring initializr]:https://start.spring.io/

---
## Project Config

### Application Properties
專案配置黨設定，根據你所選擇的資料庫進行設定：

```yaml=
spring:
  datasource:
    #驅動程式類別(若非postgresql可查詢該資料庫的驅動名稱)
    driver-class-name: org.postgresql.Driver
    #資料庫url協定(若非postgresql可上網搜尋該資料庫的url協定)
    url: jdbc:postgresql://localhost:5432/test?currentSchema=employee
    #使用者名稱
    username: postgres
    #密碼
    password: ********
  jpa:
    properties:
      hibernate:
        #格式化sql
        format_sql: 'false'
    #顯示sql
    show-sql: 'true'
    #建表模式（none、validate、update、create和create-drop）。
    hibernate:
      ddl-auto: update
```
spring.jpa.hibernate.ddl-auto：指的是服務起來時要用何種方式執行ddl指令, 有create, update, create-drop 等形式。

|mode                    |description                            |
|------------------------|---------------------------------------|
|none                    |這是MySQL的預設值，沒有對資料庫結構的更改。|
|validate                |只會和數據庫中的表進行比較，不創建新表，但是會插入新值。|
|update                  |Hibernate根據給定的實體結構改變資料庫。|
|create-drop(:warning:謹慎使用)|建立資料庫，然後在SessionFactory關閉時刪除它。|

[spring.jpa.hibernate.ddl-auto](https://docs.spring.io/spring-boot/docs/1.1.0.M1/reference/html/howto-database-initialization.html#howto-initialize-a-database-using-hibernate)
:::warning
:warning: **Warning:** 這裡選擇使用ymal格式，你也可以使用properties文件格式。
:::

---

### Define a Simple Entity
在專案中建立實體(Employee)以映射資料庫的表格欄位：
- @Id：指定Primary Key欄位
- @Table：對應資料庫Table名稱
- @GenerateedValue：指定 ID 的生成方式

JPA提供四種GenerationType標準用法TABLE、SEQUENCE、IDENTITY、AUTO


| strategy  | description                                    |
|-----------|------------------------------------------------|
|IDENTITY   |MySQL常見使用方式，ID資料庫自動生成                  |
|SEQUENCE   |根據資料庫表格序列生成ID，常搭配註解@SequenceGenerator|
|AUTO       |預設的生成策略，允許永續性提供程式選擇生成策略          |
|TABLE      |透過另外一個表格來定義ID，模擬序列                   |

[Hibernate入門之主鍵生成策略詳解](https://www.itread01.com/content/1582995908.html)
- @Data：Lombok註解，自動生成get、set用法
- @AllArgsConstructor：Lombok註解，生成一個包含所有參數的 constructor
- @NoArgsConstructor：Lombok註解，生成一個沒有參數的constructor
- @Builder：LomBok註解，自動生成set方法，通常搭配@Data出現

:::danger
:x:  **Error:** 若在Lombok註解處顯示error，IDE需加入Lombok插件，或選擇不使用Lombok，則需自行加入get、set。:fearful: 
:::

[Java - 五分鐘學會 Lombok 用法](https://kucw.github.io/blog/2020/3/java-lombok/)

加入實體:arrow_down: 
```java=
@Entity
@Data
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    @Id
    @SequenceGenerator(name = "employee_emp_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int empId;

    private String email;

    private String firstName;

    private String lastName;

    private String mobileNo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

---

### Create Repository :door: 

建立EmployeeRepositoy繼承JPARepository，從而獲得Spring為我們預先定義的多種基本資料操作方法，透過Repository來使用它最引人注目的功能是能夠在運行時從存儲庫接口自動創建存儲庫實現。





https://docs.spring.io/spring-data/jpa/docs/current/reference





