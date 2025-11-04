# JAVA-SPRING-BOOT-API

Como usar no VS Code:

Crie um projeto Spring Boot com dependências:

Spring Web

Spring Data JPA

H2 Database

Cole o código acima em src/main/java/com/example/pixverifier/PixVerifierApplication.java.

No application.properties, adicione:

spring.datasource.url=jdbc:h2:mem:pixdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

Rode com mvn spring-boot:run ou pelo botão de execução do VS Code.
