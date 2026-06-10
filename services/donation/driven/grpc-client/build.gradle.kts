val donationApi: String by project
val donationApplication: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))

    // TODO(gRPC): payment-api proto stub + gRPC 공통 모듈 의존 추가
    //   implementation(project(":payment:payment-api:payment-proto"))
    //   implementation(project(":grpc-core"))  // 공통 gRPC 모듈 (ManagedChannel 등)
}