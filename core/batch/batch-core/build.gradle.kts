dependencies{
    api(project(":batch-api"))

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.batch:spring-batch-core")

    // 로그/레코드 스타일이 기존 모듈과 맞도록(컴파일 환경에 lombok이 이미 있다면 생략 가능)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-autoconfigure")
    testImplementation("org.springframework.batch:spring-batch-core")
}
