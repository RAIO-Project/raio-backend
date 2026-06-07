package raio.grpc.server.interceptor;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class GrpcExceptionInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (IllegalArgumentException e) {
                    close(call, Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e));
                } catch (Exception e) {
                    close(call, Status.INTERNAL.withDescription("Internal gRPC server error").withCause(e));
                }
            }
        };
    }

    private <ReqT, RespT> void close(ServerCall<ReqT, RespT> call, Status status) {
        call.close(status, new Metadata());
    }
}
