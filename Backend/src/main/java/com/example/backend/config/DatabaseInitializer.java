package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Kiểm tra xem bảng PHAN_QUYEN đã tồn tại chưa
            ResultSet tables = metaData.getTables(null, null, "PHAN_QUYEN", null);
            if (!tables.next()) {
                log.info("Cơ sở dữ liệu chưa được khởi tạo. Bắt đầu thực thi db_init.sql...");
                ClassPathResource resource = new ClassPathResource("db_init.sql");
                ScriptUtils.executeSqlScript(conn, resource);
                log.info("Khởi tạo cơ sở dữ liệu và nạp dữ liệu mẫu thành công!");
            } else {
                log.info("Cơ sở dữ liệu đã tồn tại. Bỏ qua bước chạy file SQL.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra hoặc khởi tạo cơ sở dữ liệu: ", e);
        }
    }
}
