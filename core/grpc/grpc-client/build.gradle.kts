val grpcVersion = "1.75.0"

dependencies {
    api("io.grpc:grpc-api:$grpcVersion")
    api("io.grpc:grpc-core:$grpcVersion")
    api("io.grpc:grpc-netty-shaded:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}