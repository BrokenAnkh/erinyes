package erinyes.echo;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.beyondsoft.jmall.mapper.*")
public class FreyaHairApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreyaHairApplication.class);



    public static void main(String[] args) {
        SpringApplication.run(FreyaHairApplication.class, args);
    }

}




