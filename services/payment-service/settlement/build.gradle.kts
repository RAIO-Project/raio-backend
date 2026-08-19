val settlementApi: String by project
val settlementApplication: String by project
val settlementRdbAdapter: String by project
val settlementWebMvcAdapter: String by project
val settlementGrpcServerAdapter: String by project
val settlementGrpcClientAdapter: String by project

dependencies {
    api(project(settlementApi))
    api(project(settlementApplication))
    api(project(settlementRdbAdapter))
    api(project(settlementWebMvcAdapter))
    api(project(settlementGrpcServerAdapter))
    api(project(settlementGrpcClientAdapter))
}
