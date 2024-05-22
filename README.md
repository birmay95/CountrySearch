# Проект "Поиск информации по стране"
Проект "Поиск информации по стране" успешно интегрирован с базой данных PostgreSQL для эффективного хранения и управления данными. Реализована связь один ко многим с использованием аннотации @OneToMany, обеспечивающая эффективное управление связанными данными. Также реализована связь многие ко многим с использованием аннотации @ManyToMany, что значительно расширяет возможности работы с данными и их связями.

Реализованы CRUD-операции для всех сущностей, обеспечивая полный и удобный контроль над информацией. Пользователи имеют возможность создавать, читать, обновлять и удалять записи, связанные с различными аспектами информации о странах. Механизм запросов с использованием queryParams поддерживается для более гибкого и точного поиска данных, а результаты предоставляются в формате JSON, обеспечивая удобство взаимодействия с приложением.

Таким образом, проект не только успешно интегрировал базу данных и реализовал связи между сущностями, но также обеспечил полный набор CRUD-операций для управления данными, предоставляя пользователям интуитивно понятный и эффективный интерфейс для работы с информацией о странах.

Кроме того, расширена функциональность проекта, благодаря добавлению возможности использовать "кастомные" запросы и внедрению простого кэша в виде in-memory Map для оптимизации производительности. Обработаны 400 и 500 ошибки. Добавлены соответствующие обработчики и возвращаемые сообщения об ошибках. Добавлено логирование действий и ошибок с использованием аспектов. Подключен Swagger для документирования API и CheckStyle для обеспечения стилистической правильности кода. Выполнены все необходимые исправления, чтобы убрать стилистические ошибки. Добавлен POST метод для работы со списком параметров для bulk операций. Реализована работа сервиса с использованием Java 8, включая Stream API и лямбда-выражения. Покрытие Unit-тестами на более чем 80% для бизнес-логики проекта.
## Содержание
- [Технологии](#технологии)
- [Руководство по установке Spring Boot](#руководство-по-установке-spring-boot)
- [Руководство по установке и подключению базы данных PostgreSQL](#руководство-по-установке-и-подключению-базы-данных-postgresql)
- [Руководство по установке Docker и запуску приложения](#руководство-по-установке-docker-и-запуску-приложения)
- [Результаты SonarCloud](#результаты-SonarCloud)
- [Swagger](#swagger)
- [Front-End](#front-end)
- [Команда проекта](#команда-проекта)

## Технологии
- [Java Spring Boot](https://spring.io/projects/spring-boot) — это средство с открытым исходным кодом, которое упрощает использование платформ на основе Java для создания микрослужб и веб-приложений.
- [PostgreSQL](https://www.postgresql.org/) — это реляционная база данных с открытым кодом, которая поддерживается в течение 30 лет разработки и является одной из наиболее известных среди всех существующих реляционных баз данных. Популярностью у разработчиков и администраторов база данных PostgreSQL обязана своей исключительной гибкости и целостности.
- [Docker](https://www.docker.com/) — это платформа с открытым исходным кодом, предназначенная для автоматизации развертывания, масштабирования и управления приложениями в контейнерах. Docker позволяет упаковывать приложение вместе со всем его окружением и зависимостями в контейнер, который может быть перенесен на любую систему, поддерживающую Docker, независимо от того, на какой операционной системе она работает.

## Руководство по установке Spring Boot

### Шаг 1: Установка Java Development Kit (JDK)

Убедитесь, что на вашем компьютере установлен [JDK](https://www.oracle.com/java/technologies/javase-downloads.html) версии 8 или выше.

### Шаг 2: Установка среды разработки (по желанию)

Используйте любую среду разработки на ваш выбор, такую как IntelliJ IDEA, Eclipse или Visual Studio Code.

### Шаг 3: Установка Spring Boot

#### Используя Maven

1. Добавьте зависимость Spring Boot в ваш файл `pom.xml`:

   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter</artifactId>
       </dependency>
   </dependencies>
   ```

2. Создайте основной класс с методом main:

   ```Java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   
   @SpringBootApplication
   public class MySpringBootApplication {
       public static void main(String[] args) {
           SpringApplication.run(MySpringBootApplication.class, args);
       }
   }
   ```

### Шаг 4: Запуск приложения

Выполните следующую команду Maven для запуска:

```bash
mvn spring-boot:run
```

Ваше приложение будет доступно по адресу http://localhost:8080.

Поздравляю! Теперь у вас установлен и работает простой проект Spring Boot. Продолжайте разработку своего приложения, добавляйте компоненты Spring Framework, и не забудьте обратиться к официальной документации Spring Boot, чтобы узнать больше.

## Руководство по установке и подключению базы данных PostgreSQL

 PostgreSQL - мощная система управления реляционными базами данных, и вам потребуется ее установить и настроить перед использованием.

### Шаг 1: Установка PostgreSQL

```bash
sudo dnf install postgresql-server postgresql-contrib
sudo systemctl enable postgresql
sudo systemctl start postgresql
```

### Шаг 2: Настройка PostgreSQL
#### Создание пользователя и базы данных:

1. Зайдите в командную строку PostgreSQL:

   ```bash
   sudo -u postgres psql
   ```
2. Создайте пользователя:

   ```sql
   CREATE USER ваше_имя_пользователя WITH PASSWORD 'ваш_пароль';
   ```
3. Создайте базу данных:

   ```sql
   CREATE DATABASE ваша_база_данных;
   ```
   
4. Предоставьте пользователю права на базу данных:

   ```sql
   GRANT ALL PRIVILEGES ON DATABASE ваша_база_данных TO ваше_имя_пользователя;
   ```

5. Выйдите из командной строки PostgreSQL:

   ```sql
    \q
   ```

### Шаг 3: Подключение к Базе Данных из Проекта
#### Использование файла application.properties для настройки подключения:

```properties
# application.properties

# Параметры подключения к базе данных PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ваша_база_данных
spring.datasource.username=ваше_имя_пользователя
spring.datasource.password=ваш_пароль
spring.jpa.hibernate.ddl-auto=create
spring.jpa.open-in-view=true
server.error.include-message=always

# Остальные настройки приложения
# ...
```
#### Изменение файла pom.xml для включения зависимости JDBC:

```xml
<!-- pom.xml -->

<dependencies>
    <!-- Другие зависимости -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
       <scope>provided</scope>
    </dependency>
</dependencies>
```

Теперь вы успешно установили и настроили базу данных PostgreSQL и настроили подключение к ней в вашем проекте на языке Java на операционной системе Fedora! Не забудьте обновить параметры подключения в файле application.properties согласно вашим данным.

## Руководство по установке Docker и запуску приложения

Это руководство поможет вам установить Docker и Docker Compose на Fedora, а затем запустить ваше приложение, состоящее из бекенда на Java Spring Boot и фронтенда на React, в контейнерах Docker.

### Шаг 1: Установка Docker

1. Обновите индекс пакетов и установите необходимые зависимости:
    ```sh
    sudo dnf -y update
    sudo dnf -y install dnf-plugins-core
    ```

2. Добавьте репозиторий Docker:
    ```sh
    sudo dnf config-manager --add-repo https://download.docker.com/linux/fedora/docker-ce.repo
    ```

3. Установите Docker Engine:
    ```sh
    sudo dnf install docker-ce docker-ce-cli containerd.io
    ```

4. Запустите Docker и убедитесь, что он настроен на автозапуск:
    ```sh
    sudo systemctl start docker
    sudo systemctl enable docker
    ```

5. Проверьте установку, запустив тестовый контейнер:
    ```sh
    sudo docker run hello-world
    ```

### Шаг 2: Установка Docker Compose

1. Загрузите текущую стабильную версию Docker Compose:
    ```sh
    sudo curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    ```

2. Сделайте бинарный файл исполняемым:
    ```sh
    sudo chmod +x /usr/local/bin/docker-compose
    ```

3. Проверьте установку:
    ```sh
    docker-compose --version
    ```

### Шаг 3: Запуск приложения

1. Клонируйте репозиторий вашего проекта (если еще не клонировали):
    ```sh
    git clone <URL репозитория>
    cd <имя директории репозитория>
    ```

2. Убедитесь, что в вашем репозитории присутствуют файлы `Dockerfile` для бекенда и фронтенда, а также файл `docker-compose.yml`.

3. Соберите образы и запустите контейнеры:
    ```sh
    sudo docker-compose up --build
    ```

4. Приложение будет доступно по следующим адресам:
   - Бекенд (Spring Boot): `http://localhost:8080`
   - Фронтенд (React): `http://localhost:3000`

### Заключение

Теперь у вас должно быть работающее Java Spring Boot и React приложение в контейнерах Docker. Для дополнительных настроек и подробностей, пожалуйста, обратитесь к официальной документации Docker.



## Результаты SonarCloud
По этой ссылке можно посмотреть результаты SonarCloud:
https://sonarcloud.io/summary/overall?id=birmay95_CountrySearch

## Swagger
Для того, чтобы перейти на страницу swagger, нужно запустить приложение и ввести следующий url:
http://localhost:8080/swagger-ui/index.html

## Front-End
По этой ссылке можно перейти на репозиторий с кодом фронтенда:
https://github.com/birmay95/CountrySearchFrontend

## Команда проекта
Контакты для связи с разработчиком.

- [Михаил Боровенский](https://t.me/mishail_b) — Developer
