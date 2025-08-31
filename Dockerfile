# Sử dụng base image có Java 17, phù hợp với pom.xml của bạn
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Sao chép file pom.xml và các file source vào container
# Tách riêng để tận dụng cache của Docker, giúp build nhanh hơn
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Chạy Maven để build ứng dụng và tạo ra file .jar
# -DskipTests sẽ bỏ qua việc chạy test để build nhanh hơn
RUN ./mvnw package -DskipTests

# Mở cổng 8080 (cổng mặc định trong application.properties của bạn)
EXPOSE 8080

# Lệnh để khởi chạy ứng dụng khi container bắt đầu
# Thay thế 'DATN-0.0.1-SNAPSHOT.jar' nếu tên file jar của bạn khác
ENTRYPOINT ["java", "-jar", "target/DATN-0.0.1-SNAPSHOT.jar"]